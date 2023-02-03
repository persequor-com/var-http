package io.varhttp;

import io.varhttp.controllers.MyTestController;

import java.time.Duration;
import java.util.function.Consumer;
import javax.inject.Inject;

public class Launcher implements Runnable {
	private final Standalone standalone;

	@Inject
	public Launcher(Standalone standalone) {
		this.standalone = standalone;
	}

	public void setSsl() {
		standalone.setSslContext(Launcher.class.getResourceAsStream("/test.pem"), Launcher.class.getResourceAsStream("/test.key"));
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
		standalone.stop(Duration.ofSeconds(1));
	}

	public VarServlet getServlet() {
		return standalone.servlet;
	}

	public void configure(Consumer<VarConfiguration> config) {
		standalone.configure(config);
	}

}
