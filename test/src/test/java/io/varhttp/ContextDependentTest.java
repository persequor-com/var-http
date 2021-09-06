package io.varhttp;

import io.odinjector.Binder;
import io.odinjector.Context;
import io.odinjector.OdinJector;
import io.varhttp.controllers.AnotherContextDependentController;
import io.varhttp.controllers.ContextDependentController;
import io.varhttp.controllers.IMyContext;
import io.varhttp.controllers.MyFirstContext;
import io.varhttp.controllers.MySecondContext;
import io.varhttp.controllers.withfilters.FilterCatcher;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.net.HttpURLConnection;

import static org.junit.Assert.*;

public class ContextDependentTest {
	static Standalone launcher;
	static Thread thread;
	private static FilterCatcher filterCatcher;

	@BeforeClass
	public static void setup() {
		OdinJector odinJector = OdinJector.create().addContext(new OdinContext(new VarConfig().setPort(8088))).addContext(new ControllerFilterContext()).addContext(new Context1());
		OdinJector odinJector2 = OdinJector.create().addContext(new OdinContext(new VarConfig().setPort(8088))).addContext(new ControllerFilterContext()).addContext(new Context2());
		filterCatcher = odinJector.getInstance(FilterCatcher.class);
		launcher = odinJector.getInstance(Standalone.class);
		launcher.getServlet().configure(varConfiguration -> {
			varConfiguration.setControllerFactory((controllerClass -> odinJector.getInstance(controllerClass)));
			varConfiguration.addController(ContextDependentController.class);
		});
		launcher.getServlet().configure(varConfiguration -> {
			varConfiguration.setControllerFactory((controllerClass -> odinJector2.getInstance(controllerClass)));
			varConfiguration.addController(AnotherContextDependentController.class);
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
	public void controllerMethodWhichIsFilteredOut() throws Throwable {
		HttpURLConnection con = HttpClient.get("http://localhost:8088/contextdependent", "");
		String actual = HttpClient.readContent(con).toString();
		assertEquals("MyFirstContext", actual);

		con = HttpClient.get("http://localhost:8088/anothercontextdependent", "");
actual = HttpClient.readContent(con).toString();
		assertEquals("MySecondContext", actual);
	}

	private static class Context1 extends Context {
		@Override
		public void configure(Binder binder) {
			binder.bind(IMyContext.class).to(MyFirstContext.class);
		}
	}

	private static class Context2 extends Context {
		@Override
		public void configure(Binder binder) {
			binder.bind(IMyContext.class).to(MySecondContext.class);
		}
	}
}
