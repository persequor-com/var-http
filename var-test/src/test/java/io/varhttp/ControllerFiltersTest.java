package io.varhttp;

import io.odinjector.OdinJector;
import io.varhttp.controllers.withfilters.FilterCatcher;
import io.varhttp.test.VarClient;
import io.varhttp.test.VarClientHttp;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ControllerFiltersTest {
	static Launcher launcher;
	private static VarClient varClient;

	private static FilterCatcher filterCatcher;

	@BeforeClass
	public static void setup() {
		OdinJector odinJector = OdinJector.create().addContext(new OdinContext(new VarConfig().setPort(8088))).addContext(new ControllerFilterContext())
				.addContext(new UndertowContext());
		filterCatcher = odinJector.getInstance(FilterCatcher.class);
		launcher = odinJector.getInstance(Launcher.class);
		launcher.run();

		VarClientHttp varClientHttp = odinJector.getInstance(VarClientHttp.class);
		varClientHttp.withServerUrl("http://localhost:8088");
		varClient = varClientHttp;
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
		varClient.get("/muh")
				.execute()
				.isNotFound();
		assertTrue(filterCatcher.getResult().isEmpty());
	}

	@Test
	public void controllerMethodWhichIsNotFilteredOut() throws Throwable {
		varClient.get("/login")
				.execute()
				.isOk();
	}
}
