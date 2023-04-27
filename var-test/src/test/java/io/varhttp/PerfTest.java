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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

public class PerfTest {
	static PerfLauncher launcher;

	@BeforeClass
	public static void setup() throws InterruptedException, ExecutionException, TimeoutException {
		OdinJector odinJector = OdinJector.create().addContext(new OdinContext(new VarConfig().setPort(8089)));
		launcher = odinJector.getInstance(PerfLauncher.class);
		launcher.run();
		launcher.isStarted().get(5, TimeUnit.SECONDS);
	}

	@AfterClass
	public static void teardown() {
		launcher.stop();
	}

	@Test
	public void simple() throws Throwable {
		int reps = 1000;

		List<Thread> threads = new ArrayList<>();

		String body = IntStream.range(0, 200).mapToObj(in -> UUID.randomUUID().toString()).collect(Collectors.joining("-"));
		final AtomicInteger processed = new AtomicInteger(0);
		final AtomicInteger failed = new AtomicInteger(0);
		final int nrOfThreads = 100;
		for (int j = 0; j < nrOfThreads; j++) {
			Runnable t = () -> {
				for (int i = 0; i < reps; i++) {
					int classnum = (int) (Math.random() * 7) + 1;
					int methodNum = (int) (Math.random() * 5) + 1;
					String path = "http://localhost:8089/class" + classnum + "/controller" + methodNum + "/muhbuh?name=" + classnum + methodNum;
					try {
						HttpURLConnection con = HttpClient.post(path, body, "text/plain");

						String output = HttpClient.readContent(con).toString();
						if (!"muh".equals(output)) {
							System.out.printf("Expected 'muh', but controller returned: %s%n", output);
							failed.incrementAndGet();
						}
					} catch (Exception e) {
						failed.incrementAndGet();
						System.out.println(path + " failed" + failed.get());
						e.printStackTrace();
					} finally {
						processed.incrementAndGet();
					}
				}
			};
			threads.add(new Thread(t));
		}
		long s = System.currentTimeMillis();

		threads.forEach(Thread::start);
		for (Thread thread : threads) {
			thread.join();
		}
		System.out.println("nr of threads: " + nrOfThreads);
		System.out.println("nr of reads per thread: " + reps);
		System.out.printf("nr of failed/total requests: %s/%s%n", failed, processed);
		System.out.println("avg time to run: " + ((System.currentTimeMillis() - s) / (reps * nrOfThreads * 1.0d)));
		assertEquals(0, failed.get());
	}
}