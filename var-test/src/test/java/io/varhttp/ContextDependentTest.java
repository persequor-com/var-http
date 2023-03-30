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
import io.varhttp.test.HttpClient;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.Duration;
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
			baseConfiguration.configure(varConfiguration -> {
				varConfiguration.setObjectFactory((odinJector::getInstance));
				varConfiguration.addDefaultFilter(FirstFilter.class);
				varConfiguration.addController(ContextDependentController.class);
			});
			baseConfiguration.configure(varConfiguration -> {
				varConfiguration.setObjectFactory((odinJector2::getInstance));
				varConfiguration.addControllerMatcher(new AltControllerMatcher());
				varConfiguration.addController(AnotherContextDependentController.class);
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
		launcher.stop(Duration.ofSeconds(1));
	}

	@Test
	public void contextDependentControllers() throws Throwable {
		HttpURLConnection con = HttpClient.get("http://localhost:8088/contextdependent", "");
		String actual = HttpClient.readContent(con).toString();
		assertEquals("MyFirstContext", actual);

		con = HttpClient.get("http://localhost:8088/anothercontextdependent", "");
actual = HttpClient.readContent(con).toString();
		assertEquals("MySecondContext", actual);

		assertEquals(1, FirstFilter.callCount.get());
		assertEquals(2, BaseFilter.callCount.get());
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

	public static class FirstFilter implements Filter {
		static AtomicInteger callCount = new AtomicInteger(0);
		@Override
		public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
			callCount.incrementAndGet();
			chain.doFilter(request, response);
		}
	}

	public static class BaseFilter implements Filter {
		static AtomicInteger callCount = new AtomicInteger(0);
		@Override
		public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
			callCount.incrementAndGet();
			chain.doFilter(request, response);
		}
	}
}
