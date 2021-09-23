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
import org.junit.*;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

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
		launcher.configure(baseConfiguration -> {
			baseConfiguration.addDefaultFilter(BaseFilter.class);
			baseConfiguration.addDefaultVarFilter(BaseVarFilter.class);

			baseConfiguration.configure(varConfiguration -> {
				varConfiguration.setControllerFactory((controllerClass -> odinJector2.getInstance(controllerClass)));
				varConfiguration.addControllerMatcher(new AltControllerMatcher());
				varConfiguration.addController(AnotherContextDependentController.class);
			});

			baseConfiguration.configure(varConfiguration -> {
				varConfiguration.setControllerFactory((controllerClass -> odinJector.getInstance(controllerClass)));
				varConfiguration.addDefaultFilter(FirstFilter.class);
				varConfiguration.addController(ContextDependentController.class);
			});
		});
		thread = new Thread(launcher);
		thread.run();
	}
	@Before
	public void setupTests() {
		order.clear();
	}

	@After
	public void after() {
		filterCatcher.getResult().clear();
		FirstFilter.callCount.set(0);
		BaseVarFilter.callCount.set(0);
		BaseFilter.callCount.set(0);
	}

	@AfterClass
	public static void teardown() {
		launcher.stop();
	}

	@Test
	public void contextDependentControllers_contextFilters() throws Throwable {
		HttpURLConnection con = HttpClient.get("http://localhost:8088/contextdependent", "");
		String actual = HttpClient.readContent(con).toString();
		assertEquals("MyFirstContext", actual);

		assertEquals(1, FirstFilter.callCount.get());
		assertEquals(1, BaseVarFilter.callCount.get());
		assertEquals(1, BaseFilter.callCount.get());
		assertEquals(order.size(), 3);
		assertEquals(order.get(0), "BaseFilter");
		assertEquals(order.get(1), "BaseVarFilter");
		assertEquals(order.get(2), "FirstFilter");
	}

	@Test
	public void contextDependentControllers_noContextFilters() throws Throwable {
		HttpURLConnection 		con = HttpClient.get("http://localhost:8088/anothercontextdependent", "");
		String actual = HttpClient.readContent(con).toString();
		assertEquals("MySecondContext", actual);

		assertEquals(0, FirstFilter.callCount.get());
		assertEquals(1, BaseVarFilter.callCount.get());
		assertEquals(1, BaseFilter.callCount.get());
		assertEquals(order.size(), 2);
		assertEquals(order.get(0), "BaseFilter");
		assertEquals(order.get(1), "BaseVarFilter");
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

	static LinkedList<String> order = new LinkedList<>();

	public static class FirstFilter implements Filter {
		static AtomicInteger callCount = new AtomicInteger(0);
		@Override
		public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
			callCount.incrementAndGet();
			order.add(this.getClass().getSimpleName());
			chain.doFilter(request, response);
		}
	}

	public static class BaseFilter implements Filter {
		static AtomicInteger callCount = new AtomicInteger(0);
		@Override
		public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
			callCount.incrementAndGet();
			order.add(this.getClass().getSimpleName());
			chain.doFilter(request, response);
		}
	}

	public static class BaseVarFilter {
		static AtomicInteger callCount = new AtomicInteger(0);

		@FilterMethod
		public void doFilter() {
			callCount.incrementAndGet();
			order.add(this.getClass().getSimpleName());
		}
	}
}
