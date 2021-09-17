package io.varhttp;

import io.odinjector.OdinJector;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class LauncherCORSTest {
	static Launcher launcher;
	static Thread thread;
	private final String HTTP_HOST = "localhost:8088";
	private final String HTTP_HOST_DIFFERENT = "var:3000";
	private final String HTTP_ORIGIN_SAME_HOST = "http://" + HTTP_HOST;

	@BeforeClass
	public static void setup() {
		OdinJector odinJector = OdinJector.create().addContext(new OdinContext(new VarConfig().setPort(8088)));
		launcher = odinJector.getInstance(Launcher.class);
		launcher.setCORS();
		thread = new Thread(launcher);
		thread.run();
	}

	@AfterClass
	public static void teardown() {
		launcher.stop();
	}

	@Test
	public void happyPath_prefightRequest() throws Throwable {
		HttpURLConnection  con = HttpClient.options("http://localhost:8088/listObject");

		con.setRequestProperty("Origin", HTTP_ORIGIN_SAME_HOST);
		con.setRequestProperty("Host", "var:3000");

		con.setRequestProperty("Access-Control-Request-Headers","content-type,x-requested-with");
		con.setRequestProperty("Access-Control-Request-Method", "GET");
		assertTrue("No Origin", con.getRequestProperties().containsKey("Origin"));
		Map<String, List<String>> headers = HttpClient.readHeaders(con);
		con.disconnect();

		assertTrue("Missing header Access-control-allow-credentials", headers.containsKey("Access-control-allow-credentials"));
		assertTrue("Missing header Access-control-allow-origin", headers.containsKey("Access-control-allow-origin"));
		assertTrue("Missing header Access-control-allow-methods", headers.containsKey("Access-control-allow-methods"));
		assertTrue("Missing header Access-control-allow-headers", headers.containsKey("Access-control-allow-headers"));
		assertTrue("Missing header Access-control-max-age", headers.containsKey("Access-control-max-age"));

		assertEquals(headers.get("Access-control-allow-credentials").get(0), "true");
		assertEquals(headers.get("Access-control-allow-origin").get(0), "http://localhost:8088");
		assertEquals(headers.get("Access-control-allow-methods").get(0), "POST");
		assertEquals(headers.get("Access-control-allow-headers").get(0), "content-type,x-requested-with");
		assertEquals(headers.get("Access-control-max-age").get(0), "60");
	}

	@Test
	public void prefightRequest_sameHostAndOrigin() throws Throwable {
		HttpURLConnection  con = HttpClient.options(HTTP_ORIGIN_SAME_HOST + "/listObject");

		con.setRequestProperty("Origin", HTTP_ORIGIN_SAME_HOST);
		con.setRequestProperty("Host", HTTP_HOST);

		con.setRequestProperty("Access-Control-Request-Headers","content-type,x-requested-with");
		con.setRequestProperty("Access-Control-Request-Method", "GET");

		Map<String, List<String>> headers = HttpClient.readHeaders(con);
		con.disconnect();

		assertFalse("Header Access-control-allow-credentials is present", headers.containsKey("Access-control-allow-credentials"));
		assertFalse("Header Access-control-allow-origin is present", headers.containsKey("Access-control-allow-origin"));
		assertFalse("Header Access-control-allow-methods is present", headers.containsKey("Access-control-allow-methods"));
		assertFalse("Header Access-control-allow-headers is present", headers.containsKey("Access-control-allow-headers"));
		assertFalse("Header Access-control-max-age is present", headers.containsKey("Access-control-max-age"));
	}

	@Test
	public void happyPath_postRequest() throws Throwable {
		HttpURLConnection con = HttpClient.post(HTTP_ORIGIN_SAME_HOST + "/listObject",
				"[{\"id\":\"id1\",\"name\":\"name1\"}]", connection -> {
			connection.setRequestProperty("Origin", HTTP_ORIGIN_SAME_HOST);
			connection.setRequestProperty("Host", HTTP_HOST_DIFFERENT);

			connection.setRequestProperty("Content-Type","application/json");
		});

		Map<String, List<String>> headers = HttpClient.readHeaders(con);

		assertTrue("Missing header Access-control-allow-credentials", headers.containsKey("Access-control-allow-credentials"));
		assertTrue("Missing header Access-control-allow-origin", headers.containsKey("Access-control-allow-origin"));

		assertEquals(headers.get("Access-control-allow-credentials").get(0), "true");
		assertEquals(headers.get("Access-control-allow-origin").get(0), "http://localhost:8088");

		assertFalse("Header Access-control-allow-methods is present", headers.containsKey("Access-control-allow-methods"));
		assertFalse("Header Access-control-allow-headers is present", headers.containsKey("Access-control-allow-headers"));
		assertFalse("Header Access-control-max-age is present", headers.containsKey("Access-control-max-age"));
	}

	@Test
	public void postRequest_sameHostAndOrigin() {
		HttpURLConnection con = HttpClient.post(HTTP_ORIGIN_SAME_HOST + "/listObject",
				"[{\"id\":\"id1\",\"name\":\"name1\"}]", connection -> {
					connection.setRequestProperty("Origin", HTTP_ORIGIN_SAME_HOST);
					connection.setRequestProperty("Host", HTTP_HOST);

					connection.setRequestProperty("Content-Type","application/json");
				});

		Map<String, List<String>> headers = HttpClient.readHeaders(con);

		assertFalse("Header Access-control-allow-credentials is present", headers.containsKey("Access-control-allow-credentials"));
		assertFalse("Header Access-control-allow-origin is present", headers.containsKey("Access-control-allow-origin"));
		assertFalse("Header Access-control-allow-methods is present", headers.containsKey("Access-control-allow-methods"));
		assertFalse("Header Access-control-allow-headers is present", headers.containsKey("Access-control-allow-headers"));
		assertFalse("Header Access-control-max-age is present", headers.containsKey("Access-control-max-age"));
	}
}