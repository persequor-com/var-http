package io.varhttp;

import io.odinjector.OdinJector;
import io.varhttp.controllers.withfilters.FilterCatcher;
import io.varhttp.controllers.withfilters.LoggingFilter;
import io.varhttp.test.HttpClient;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DefaultFiltersTest {
	static Launcher launcher;
	static Thread thread;
	private static FilterCatcher filterCatcher;

	@BeforeClass
	public static void setup() {
		OdinJector odinJector = OdinJector.create().addContext(new OdinContext(new VarConfig().setPort(8088)));
		filterCatcher = odinJector.getInstance(FilterCatcher.class);

		launcher = odinJector.getInstance(Launcher.class);
		launcher.configure(config -> {
			config.addDefaultFilter(LoggingFilter.class);
			config.addDefaultVarFilter(DefaultFiltersTest.class);
		});
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
		filterCatcher = null;
	}

	@Test
	public void defaultFilter() throws Throwable {
		HttpURLConnection con = HttpClient.get("http://localhost:8088/my-test", "");
		HttpClient.readContent(con);
		List<String> result = filterCatcher.getResult();
		System.out.println("----------> " + result);
		assertEquals(
				Arrays.asList(
						"Logging was called before",
						"/my-test",
						"Logging was called after"
				)
				, result);
	}

	@FilterMethod
	public void myFilter(RequestHeader requestHeader) {
		filterCatcher.add(requestHeader.getPath());
	}
}