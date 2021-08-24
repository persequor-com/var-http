package io.varhttp;

import io.odinjector.OdinJector;
import io.varhttp.controllers.withfilters.FilterCatcher;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.net.HttpURLConnection;
import java.util.List;

import static org.junit.Assert.*;

public class ControllerFiltersTest {
	static Launcher launcher;
	static Thread thread;
	private static FilterCatcher filterCatcher;

	@BeforeClass
	public static void setup() {
		OdinJector odinJector = OdinJector.create().addContext(new OdinContext(new VarConfig().setPort(8088))).addContext(new ControllerFilterContext());
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
	public void controllerMethodWhichIsFilteredOut() throws Throwable {
		HttpURLConnection con = HttpClient.get("http://localhost:8088/muh", "");
		try {
			HttpClient.readContent(con);
			fail();
		} catch (FileNotFoundException e) {
			// This exception is expected
			assertTrue(filterCatcher.getResult().isEmpty());
		}
	}

	@Test
	public void controllerMethodWhichIsNotFilteredOut() throws Throwable {
		HttpURLConnection con = HttpClient.get("http://localhost:8088/login", "");

		HttpClient.readContent(con);
	}
}
