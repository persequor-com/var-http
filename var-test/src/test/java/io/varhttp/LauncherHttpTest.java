package io.varhttp;

import io.odinjector.OdinJector;
import io.varhttp.test.VarClientHttp;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class LauncherHttpTest extends LauncherTestBase {

	private static Launcher launcher;

	@BeforeClass
	public static void setup() {
		OdinJector odinJector = OdinJector.create().addContext(new OdinContext(new VarConfig().setPort(8088)))
				.addContext(new UndertowContext());
		launcher = odinJector.getInstance(Launcher.class);
		launcher.run();

		VarClientHttp varClientHttp = odinJector.getInstance(VarClientHttp.class);
		varClientHttp.withServerUrl("http://localhost:8088");
		varClient = varClientHttp;
	}


	@AfterClass
	public static void teardown() {
		launcher.stop();
	}

}
