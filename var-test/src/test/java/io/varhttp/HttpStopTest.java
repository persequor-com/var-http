package io.varhttp;

import io.odinjector.OdinJector;
import io.varhttp.controllers.MyStopTestController;
import io.varhttp.test.VarClient;
import io.varhttp.test.VarClientHttp;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;
import java.net.ConnectException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class HttpStopTest {

	private static Launcher launcher;

	@BeforeClass
	public static void setup() {
		OdinJector odinJector = OdinJector.create().addContext(new OdinContext(new VarConfig().setPort(8088)));
		launcher = odinJector.getInstance(Launcher.class);
		launcher.run();

		VarClientHttp varClientHttp = odinJector.getInstance(VarClientHttp.class);
		varClientHttp.withServerUrl("http://localhost:8088");
		varClient = varClientHttp;
	}


	protected static VarClient varClient;

	@Test
	public void stopTest() throws Throwable {
		AtomicReference<String> controllerResult = new AtomicReference<>();

		Thread longLivingControllerCall = new Thread(() -> {
			controllerResult.set(varClient.get("/controllable-endpoint")
					.execute()
					.getContent());
		});
		// call long-living controller
		longLivingControllerCall.start();
		MyStopTestController.awaitStart();

		// send stop call to http server
		Thread stopperThread = launcher.stop();
		Thread.sleep(50);

		// ensure no new requests are accepted
		varClient.get("/my-test")
				.execute()
				.assertStatusCode(HttpServletResponse.SC_SERVICE_UNAVAILABLE);

		// finish long-living controller
		MyStopTestController.finishWork();
		longLivingControllerCall.join();

		// ensure long-living controller has finished successfully
		assertEquals("success!", controllerResult.get());

		//wait for full server stop
		stopperThread.join();

		try {
			varClient.get("/my-test").execute();
			fail("Test should fail contacting the server after the full stop");
		} catch (RuntimeException exception) {
			assertTrue(exception.getCause() instanceof ConnectException);
		}
	}

}
