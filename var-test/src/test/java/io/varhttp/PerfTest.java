package io.varhttp;

import io.odinjector.OdinJector;
import io.varhttp.test.HttpClient;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class PerfTest {
	static PerfLauncher launcher;
	static Thread thread;

	@BeforeClass
	public static void setup() throws InterruptedException, ExecutionException, TimeoutException {
		OdinJector odinJector = OdinJector.create().addContext(new OdinContext(new VarConfig().setPort(8089)))
				.addContext(new UndertowContext());
		launcher = odinJector.getInstance(PerfLauncher.class);
		thread = new Thread(launcher);
		thread.run();
		launcher.isStarted().get(5, TimeUnit.SECONDS);
	}

	@AfterClass
	public static void teardown() {
		launcher.stop();
	}

	@Test
	public void simple() throws Throwable {
		int reps = 300;

		List<Runnable> threads = new ArrayList<>();

		String body = IntStream.range(0,200).mapToObj(in -> UUID.randomUUID().toString()).collect(Collectors.joining("-"));
		AtomicInteger failed = new AtomicInteger(0);
		for(int j=0;j<8;j++) {
			Runnable t = () -> {
				for (int i = 0; i < reps; i++) {
					int classnum = (int) (Math.random() * 7) + 1;
					int methodNum = (int) (Math.random() * 5) + 1;
					String path = "http://localhost:8089/class" + classnum + "/controller" + methodNum+"/muhbuh?name="+classnum+methodNum;
					try {
						HttpURLConnection con = HttpClient.post(path, body, "text/plain");

						String output = HttpClient.readContent(con).toString();
						assertEquals("muh", output);
					} catch (Exception e) {
						System.out.println(path+" failed");
						e.printStackTrace();
						failed.incrementAndGet();
					}
				}
			};
			threads.add(t);
		}
		long s = System.currentTimeMillis();
		ExecutorService tp = Executors.newCachedThreadPool();

		threads.forEach(tp::execute);
		tp.shutdown();
		tp.awaitTermination(10, TimeUnit.SECONDS);
		assertEquals(0, failed.get());
		System.out.println("avg time to run: "+((System.currentTimeMillis()-s)/(reps*8*1.0d)));
	}


}