package io.varhttp;

import io.odinjector.OdinJector;
import io.varhttp.test.HttpClient;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class NotFoundControllerTest {
	static Launcher launcher;
	static Thread thread;
	private final String BASE_URL = "http://localhost:8088";
	static final String CUSTOM_HEADER = "My-custom-header";

	@BeforeClass
	public static void setup() {
		OdinJector odinJector = OdinJector.create()
				.addContext(new OdinContext(new VarConfig().setPort(8088)))
				.addContext(new UndertowContext());

		launcher = odinJector.getInstance(Launcher.class);
		launcher.configure(config -> {
			config.addDefaultVarFilter(NotFoundControllerTest.class);
			config.setNotFoundController(NotFoundControllerTest.class);

			config.configure(apiConfig-> {
				apiConfig.setBasePath("/api/v2");
				try {
					apiConfig.setNotFoundController(NotFoundControllerTest.class, NotFoundControllerTest.class.getMethod("otherDefaultController", HttpServletResponse.class));
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				}
			});
		});
		thread = new Thread(launcher);
		thread.run();
	}

	@AfterClass
	public static void teardown() {
		launcher.stop();
	}

	@Test
	public void nonExistingController() throws Throwable {
		HttpURLConnection  con = HttpClient.options(BASE_URL + "/kraken");
		Map<String, List<String>> headers = HttpClient.readHeaders(con);

		assertEquals(200, con.getResponseCode());
		assertTrue("Missing custom header", headers.containsKey(CUSTOM_HEADER));
	}

	@Test
	public void controllerExits_set404() throws Throwable {
		HttpURLConnection  con = HttpClient.get(BASE_URL + "/kraken", "");
		Map<String, List<String>> headers = HttpClient.readHeaders(con);

		assertEquals(404, con.getResponseCode());
		assertTrue("Missing custom header", headers.containsKey(CUSTOM_HEADER));
	}

	@Test
	public void controllerExitsNonExiting() throws Throwable {
		HttpURLConnection  con = HttpClient.get(BASE_URL + "/api/v2" + "/kraken", "");
		Map<String, List<String>> headers = HttpClient.readHeaders(con);

		assertEquals(200, con.getResponseCode());
		assertTrue("Missing custom header", headers.containsKey("New-header"));
	}

	@NotFoundController
	public void defaultController(HttpServletRequest request, HttpServletResponse response) {
		if(!request.getMethod().equals(HttpMethod.OPTIONS.toString())) {
			response.setStatus(404);
		}
	}

	public void otherDefaultController(HttpServletResponse response) {
		response.setHeader("New-header", "new-header");
	}

	@FilterMethod
	public void hello(HttpServletResponse response) {
		response.addHeader(CUSTOM_HEADER, "hello");
	}
}