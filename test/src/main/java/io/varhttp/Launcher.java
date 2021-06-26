package io.varhttp;

import io.varhttp.controllers.MyTestController;

import javax.inject.Inject;

public class Launcher implements Runnable {
	private Standalone standalone;

	@Inject
	public Launcher(Standalone standalone) {
		this.standalone = standalone;
	}

	@Override
	public void run() {
		standalone.addControllerPackage(MyTestController.class.getPackage());
		standalone.run();
	}

	public void stop() {
		standalone.stop();
	}
}
