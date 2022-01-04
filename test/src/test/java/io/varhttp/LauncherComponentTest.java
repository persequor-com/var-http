package io.varhttp;

import io.odinjector.OdinJector;
import io.varhttp.test.VarClientServerless;
import org.junit.Before;
import org.junit.BeforeClass;

public class LauncherComponentTest extends LauncherTestBase {


	private static Serializer serializer;
	private static VarServlet varServlet;

	@BeforeClass
	public static void classSetup() {
		OdinJector odinJector = OdinJector.create().addContext(new OdinContext(new VarConfig().setPort(8088)));
		Launcher launcher = odinJector.getInstance(Launcher.class);
		varServlet = launcher.getServlet();
		varServlet.configure(launcher::configure);

		serializer = odinJector.getInstance(Serializer.class);
	}

	@Before
	public void setup() throws Throwable {
		varClient = new VarClientServerless(varServlet, serializer);
	}
}
