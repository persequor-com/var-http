package io.varhttp;

import com.sun.net.httpserver.HttpServer;

import javax.inject.Inject;
import java.io.IOException;
import java.net.InetSocketAddress;

public class Standalone implements Runnable {
	protected VarServlet servlet;
	protected ControllerMapper controllerMapper;
	protected VarConfig varConfig;
	private HttpServer server;

	@Inject
	public Standalone(VarServlet servlet, ControllerMapper controllerMapper, VarConfig varConfig) {
		this.servlet = servlet;
		this.controllerMapper = controllerMapper;
		this.varConfig = varConfig;
	}

	public void addControllerPackage(Package controllerPackage) {
		controllerMapper.map(servlet, controllerPackage.getName());
	}

	@Override
	public void run() {
		try {
			server = HttpServer.create(new InetSocketAddress(varConfig.getPort()), 0);
			server.createContext("/", new VarHttpContext(servlet));
			server.setExecutor(null); // creates a default executor
			server.start();
		} catch (IOException exception) {
			throw new VarInitializationException(exception);
		}
	}

	public void stop() {
		server.stop(0);
	}
}
