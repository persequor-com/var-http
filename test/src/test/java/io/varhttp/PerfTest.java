package io.varhttp;

import io.odinjector.OdinJector;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.HttpURLConnection;

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
		int reps = 300;
		long s = System.currentTimeMillis();
		for(int i=0;i<reps;i++) {
			int classnum = (int) (Math.random() * 39)+1;
			int methodNum = (int) (Math.random() * 39)+1;
			HttpURLConnection con = HttpClient.get("http://localhost:8088/controller" + classnum + "/method" + methodNum, "");

			HttpClient.readContent(con);
		}
		System.out.println("avg time to run: "+((System.currentTimeMillis()-s)/(reps*1.0d)));
	}


}