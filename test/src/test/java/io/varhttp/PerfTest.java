package io.varhttp;

import io.odinjector.OdinJector;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class PerfTest {
	static PerfLauncher launcher;
	static Thread thread;

	@BeforeClass
	public static void setup() {
		OdinJector odinJector = OdinJector.create().addContext(new OdinContext(new VarConfig().setPort(8088)));
		launcher = odinJector.getInstance(PerfLauncher.class);
		thread = new Thread(launcher);
		thread.run();
	}

	@AfterClass
	public static void teardown() {
		launcher.stop();
	}

	@Test
	public void simple() throws Throwable {
		int reps = 10;

		List<Runnable> threads = new ArrayList<>();
		for(int j=0;j<8;j++) {
			Runnable t = () -> {
				for (int i = 0; i < reps; i++) {
					try {
						int classnum = (int) (Math.random() * 39) + 1;
						int methodNum = (int) (Math.random() * 39) + 1;
						HttpURLConnection con = HttpClient.get("http://localhost:8088/controller" + classnum + "/method" + methodNum, "");

						HttpClient.readContent(con);
					} catch (Exception e) {
						throw new RuntimeException(e);
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
		System.out.println("avg time to run: "+((System.currentTimeMillis()-s)/(reps*8*1.0d)));
	}


}