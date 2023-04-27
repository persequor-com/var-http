package io.varhttp;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.net.ssl.SSLContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;


/**
 * Use undertow version instead. Since it has some bugs fixed and more features implemented(e.g. websockets)
 */
@Deprecated
public class Standalone implements Runnable {
	private final static Logger logger = LoggerFactory.getLogger(Standalone.class);
	private final CompletableFuture<Boolean> started = new CompletableFuture<>();
	protected VarServlet servlet;

	protected VarConfig varConfig;
	private final HttpServerFactory serverFactory;
	private HttpServer server;
	private final Map<String, HttpServlet> servlets = new LinkedHashMap<>();
	private ExecutorService executor;

	@Inject
	public Standalone(VarConfig varConfig, Provider<ParameterHandler> parameterHandlerProvider, ControllerMapper controllerMapper,
					  ObjectFactory objectFactory, ControllerFilter controllerFilter, HttpServerFactory serverFactory) {
		this.varConfig = varConfig;
		this.servlet = new VarServlet(varConfig, parameterHandlerProvider.get(), controllerMapper, objectFactory, controllerFilter, null, null);
		servlets.put("/", servlet);
		this.serverFactory = serverFactory;
	}

	public void configure(Consumer<VarConfiguration> configuration) {
		servlet.configure(configuration);
	}

	public void registerServlet(String path, HttpServlet servlet) {
		servlets.put(path, servlet);
	}

	@Override
	public void run() {
		server = serverFactory.getServer();
		for (Map.Entry<String, HttpServlet> servlet : servlets.entrySet()) {
			try {
				servlet.getValue().init(new JdkServletConfig(servlet.getValue()));
			} catch (ServletException e) {
				throw new IllegalStateException(e);
			}
			server.createContext(servlet.getKey(), new JdkHttpContext(servlet.getValue(), varConfig));
		}
		if (executor == null) {
			executor = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("var-http-thread-pool-%d").build());
		}
		server.setExecutor(executor);
		server.start();
		started.complete(true);
		logger.info("var-http started");
	}

	public void stop() {
		if (server != null) {
			server.stop(1); //stop listening and await(block) for at most 1 sec while all the current requests are done
		}
		if (executor != null) {
			executor.shutdownNow();
		}
	}

	public VarServlet getServlet() {
		return servlet;
	}

	public CompletableFuture<Boolean> getStarted() {
		return started;
	}

	public void setSslContext(InputStream x509Certificate, InputStream privateKey) {
		serverFactory.setSslContext(x509Certificate, privateKey);
	}

	public void setSslContext(SSLContext sslContext) {
		serverFactory.setSslContext(sslContext);
	}

	public void setSslContext(KeyStore keyStore, char[] password) {
		serverFactory.setSslContext(keyStore, password);
	}

	/**
	 * In case no executor is set the default one will be used: Executors.newCachedThreadPool
	 *
	 * @param executor an executor to be used to process HTTP requests
	 */
	public void setExecutor(ExecutorService executor) {
		this.executor = executor;
	}
}
