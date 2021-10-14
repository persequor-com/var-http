package io.varhttp;

import io.varhttp.controllers.MyTestController;
import java.util.function.Consumer;
import javax.inject.Inject;

public class Launcher implements Runnable {
	private Standalone standalone;

	@Inject
	public Launcher(Standalone standalone) {
		this.standalone = standalone;
	}

	public void setSsl() {
		standalone.setSslContext(Launcher.class.getResourceAsStream("/test.pem"), Launcher.class.getResourceAsStream("/test.key"));
	}

	@Override
	public void run() {
		standalone.configure(config -> {
			config.addControllerMatcher(new AltControllerMatcher());
			config.addControllerPackage(MyTestController.class.getPackage());
		});
		standalone.run();
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
