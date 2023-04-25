package io.varhttp;

import io.odinjector.OdinJector;
import io.varhttp.filterorder.DefaultFilter1;
import io.varhttp.filterorder.DefaultFilter2;
import io.varhttp.filterorder.DefaultFilter3;
import io.varhttp.filterorder.DefaultFilter4;
import io.varhttp.controllers.withfilters.FilterCatcher;
import io.varhttp.filterorder.FilterControllerClass;
import io.varhttp.filterorder.ShouldNotBeRunFilterInner;
import io.varhttp.filterorder.ShouldNotBeRunFilterOnTheSide;
import io.varhttp.test.HttpClient;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.HttpURLConnection;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FilterOrderTest {
	static VarUndertow launcher;
	static Thread thread;
	private static FilterCatcher filterCatcher;

	@BeforeClass
	public static void setup() {
		OdinJector odinJector = OdinJector.create().addContext(new OdinContext(new VarConfig().setPort(8088))).addContext(new UndertowContext());
		filterCatcher = odinJector.getInstance(FilterCatcher.class);
		launcher = odinJector.getInstance(VarUndertow.class);
		launcher.configure(config -> {
			config.addDefaultVarFilter(DefaultFilter1.class);
			config.addDefaultVarFilter(DefaultFilter2.class);
			config.configure(configInner -> {
				configInner.configure(configFurtherIn -> {
					// This filter should not be run for FilterControllerClass as it is in an inner configurataion
					configFurtherIn.addDefaultVarFilter(ShouldNotBeRunFilterInner.class);
				});
				configInner.addDefaultVarFilter(DefaultFilter3.class);
				configInner.addControllerPackage(FilterControllerClass.class.getPackage());
				configInner.addDefaultVarFilter(DefaultFilter4.class);
			});
			config.configure(configOnTheSide -> {
				// This filter should not be run for FilterControllerClass as it is in an configurataion next to
				configOnTheSide.addDefaultVarFilter(ShouldNotBeRunFilterOnTheSide.class);
			});
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
	}

	@Test
	public void fullFilterOrder() throws Throwable {
		HttpURLConnection con = HttpClient.get("http://localhost:8088/filter-order", "");
		HttpClient.readContent(con);
		List<String> result = filterCatcher.getResult();
		assertEquals("in;DefaultFilter1\n" +
				"in;DefaultFilter2\n" +
				"in;DefaultFilter3\n" +
				"in;DefaultFilter4\n" +
				"in;PackageFilter1\n" +
				"in;PackageFilter2\n" +
				"in;ClassFilter1\n" +
				"in;ClassFilter2\n" +
				"in;MethodFilter1\n" +
				"in;MethodFilter2\n" +
				"in;OverridingFilter\n" +
				"controller\n" +
				"out;OverridingFilter\n" +
				"out;MethodFilter2\n" +
				"out;MethodFilter1\n" +
				"out;ClassFilter2\n" +
				"out;ClassFilter1\n" +
				"out;PackageFilter2\n" +
				"out;PackageFilter1\n" +
				"out;DefaultFilter4\n" +
				"out;DefaultFilter3\n" +
				"out;DefaultFilter2\n" +
				"out;DefaultFilter1", String.join("\n", result));
	}
}