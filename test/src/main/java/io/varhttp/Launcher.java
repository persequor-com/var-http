package io.varhttp;

import io.varhttp.controllers.MyTestController;

import javax.inject.Inject;
import javax.net.ssl.SSLContext;

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
		standalone.addControllerPackage(MyTestController.class.getPackage());
		standalone.run();
	}

	public void stop() {
		standalone.stop();
	}

	public VarServlet getServlet() {
		return standalone.servlet;
	}
}
