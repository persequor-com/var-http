package io.varhttp;

import io.odinjector.OdinJector;
import io.varhttp.test.HttpClient;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.HttpURLConnection;

import static org.junit.Assert.assertEquals;

public class HttpsTest {
	static Launcher launcher;
	static Thread thread;

	@BeforeClass
	public static void setup() throws InterruptedException {
		OdinJector odinJector = OdinJector.create().addContext(new OdinContext(new VarConfig().setPort(8089)));
		launcher = odinJector.getInstance(Launcher.class);
		launcher.setSsl();
		thread = new Thread(launcher);
		thread.run();
	}

	@AfterClass
	public static void teardown() {
		launcher.stop();
	}

	@Test
	public void simple() throws Throwable {
		HttpURLConnection con = HttpClient.get("https://localhost:8089/my-test", "");

		StringBuffer content = HttpClient.readContent(con);

		assertEquals("Simple string", content.toString());
	}
}
