package io.varhttp;

import io.odinjector.OdinJector;
import io.varhttp.controllers.withfilters.FilterCatcher;
import io.varhttp.test.HttpClient;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FiltersTest {
	static Launcher launcher;
	static Thread thread;
	private static FilterCatcher filterCatcher;

	@BeforeClass
	public static void setup() {
		OdinJector odinJector = OdinJector.create().addContext(new OdinContext(new VarConfig().setPort(8088)))
				.addContext(new UndertowContext());
		filterCatcher = odinJector.getInstance(FilterCatcher.class);
		launcher = odinJector.getInstance(Launcher.class);
		thread = new Thread(launcher);
		thread.run();
	}

	@After
	public void after() {
		filterCatcher.getResult().clear();
	}

	@AfterClass
	public static void teardown() {
		launcher.stop();
	}

	@Test
	public void simple() throws Throwable {
		HttpURLConnection con = HttpClient.get("http://localhost:8088/muh", "");
		HttpClient.readContent(con);
		List<String> result = filterCatcher.getResult();
		assertEquals("Logging was called before\nAuthentication filter\nAuthorize for role: Admin\nFilter 1 was called\n" +
				"Filter2 before proceed\nMuh was called\nFilter2 after proceed\nLogging was called after", String.join("\n", result));
	}

	@Test
	public void overrideAuthorizationAnnotation() throws Throwable {
		HttpURLConnection con = HttpClient.get("http://localhost:8088/login", "");
		HttpClient.readContent(con);
		List<String> result = filterCatcher.getResult();
		assertEquals("Logging was called before\nAuthentication filter\nAuthorize for role: None\nLogin was called\nLogging was called after", String.join("\n", result));
	}

}