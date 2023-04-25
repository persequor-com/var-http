package io.varhttp;

import io.varhttp.controllers.MyTestController;
import java.util.function.Consumer;
import javax.inject.Inject;

public class Launcher implements Runnable {
	private final VarUndertow standalone;

	@Inject
	public Launcher(VarUndertow standalone) {
		this.standalone = standalone;
	}

	public void setSsl() {
//		standalone.setSslContext(Launcher.class.getResourceAsStream("/test.pem"), Launcher.class.getResourceAsStream("/test.key"));
	}

	@Override
	public void run() {
		standalone.configure(this::configure);
		standalone.run();
	}

	public void configure(VarConfiguration varConfiguration) {
		varConfiguration.addControllerMatcher(new AltControllerMatcher());
		varConfiguration.addControllerPackage(MyTestController.class.getPackage());
	}

	public void stop() {
		standalone.stop();
	}

	public VarServlet getServlet() {
		return standalone.servlet;
	}

	public void configure(Consumer<VarConfiguration> config) {
		standalone.configure(config);
	}

}
