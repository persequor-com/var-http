package io.varhttp;

import io.varhttp.controllers.MyTestController;

import javax.inject.Inject;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class LauncherUndertow implements Runnable {
	private final VarUndertow varUndertow;

	@Inject
	public LauncherUndertow(VarUndertow varUnderto) {
		this.varUndertow = varUnderto;
	}


	@Override
	public void run() {
		varUndertow.configure(this::configure);
		varUndertow.run();
	}

	public void configure(VarConfiguration varConfiguration) {
		varConfiguration.addControllerMatcher(new AltControllerMatcher());
		varConfiguration.addControllerPackage(MyTestController.class.getPackage());
	}

	public void stop() {
		varUndertow.stop();
	}

	public VarServlet getServlet() {
		return varUndertow.servlet;
	}

	public void configure(Consumer<VarConfiguration> config) {
		varUndertow.configure(config);
	}

	public Future<Boolean> isStarted() {
		return varUndertow.getStarted();
	}
}
