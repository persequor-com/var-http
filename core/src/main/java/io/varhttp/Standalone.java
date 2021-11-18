package io.varhttp;

import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class Standalone implements Runnable {
	private final static Logger logger = LoggerFactory.getLogger(Standalone.class);
	private final CompletableFuture<Boolean> started = new CompletableFuture<>();
	protected VarServlet servlet;

	protected VarConfig varConfig;
	private final HttpServerFactory serverFactory;
	private HttpServer server;
	private final Map<String, HttpServlet> servlets = new LinkedHashMap<>();

	@Inject
	public Standalone(VarConfig varConfig, Provider<ParameterHandler> parameterHandlerProvider, ControllerMapper controllerMapper,
					  ObjectFactory objectFactory, ControllerFilter controllerFilter, HttpServerFactory serverFactory) {
		this.varConfig = varConfig;
		this.servlet = new VarServlet(parameterHandlerProvider.get(), controllerMapper, objectFactory, controllerFilter);
		servlets.put("/", servlet);
		this.serverFactory = serverFactory;
	}

	public void configure(Consumer<VarConfiguration> configuration) {
		servlet.configure(configuration);
	}

	public void registerServlet(String path, HttpServlet servlet){
		servlets.put(path, servlet);
	}

	@Override
	public void run() {
		server = serverFactory.getServer();
		for (Map.Entry<String, HttpServlet> servlet : servlets.entrySet()) {
			try {
				servlet.getValue().init(new VarServletConfig(servlet.getValue()));
			} catch (ServletException e) {
				throw new IllegalStateException(e);
			}
			server.createContext(servlet.getKey(), new VarHttpContext(servlet.getValue(), varConfig));
		}
		server.setExecutor(Executors.newCachedThreadPool());
		server.start();
		started.complete(true);
		logger.info("var-http started");
	}

	public void stop() {
		server.stop(0);
	}

	public VarServlet getServlet() {
		return servlet;
	}

	public CompletableFuture<Boolean> getStarted() {
		return started;
	}
}
