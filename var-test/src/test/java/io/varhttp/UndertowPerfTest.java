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
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

public class UndertowPerfTest {
	static LauncherUndertow launcher;
	static Thread thread;

	@BeforeClass
	public static void setup() throws InterruptedException, ExecutionException, TimeoutException {
		OdinJector odinJector = OdinJector.create().addContext(new OdinContext(new VarConfig().setPort(8088))).addContext(new UndertowContext());
		launcher = odinJector.getInstance(LauncherUndertow.class);
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
		int numberOfThreads = 50;
		AtomicInteger failed = new AtomicInteger(0);
		for(int j=0;j<numberOfThreads;j++) {
			Runnable t = () -> {
				for (int i = 0; i < reps; i++) {
					int classnum = (int) (Math.random() * 7) + 1;
					int methodNum = (int) (Math.random() * 5) + 1;
					String path = "http://localhost:8088/class" + classnum + "/controller" + methodNum+"/muhbuh?name="+classnum+methodNum;
					System.out.println(path);
					try {
						HttpURLConnection con = HttpClient.post(path, body, "text/plain");
						assertEquals(200,con.getResponseCode());
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
		System.out.println("avg time to run: "+((System.currentTimeMillis()-s)/(reps*numberOfThreads*1.0d)));
	}


}
