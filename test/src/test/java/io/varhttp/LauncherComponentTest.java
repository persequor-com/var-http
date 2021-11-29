package io.varhttp;

import io.odinjector.OdinJector;
import io.varhttp.test.VarClientServerless;
import org.junit.BeforeClass;

public class LauncherComponentTest extends LauncherTestBase {

	@BeforeClass
	public static void setup() throws Throwable {
		OdinJector odinJector = OdinJector.create().addContext(new OdinContext(new VarConfig().setPort(8088)));
		Launcher launcher = odinJector.getInstance(Launcher.class);
		VarServlet varServlet = launcher.getServlet();
		varServlet.configure(launcher::configure);

		Serializer serializer = odinJector.getInstance(Serializer.class);

		varClient = new VarClientServerless(varServlet, serializer);
	}
}
