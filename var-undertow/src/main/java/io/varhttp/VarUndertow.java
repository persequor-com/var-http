package io.varhttp;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.GracefulShutdownHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.InstanceHandle;
import io.undertow.servlet.api.ServletInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnio.Xnio;
import org.xnio.XnioWorker;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Duration;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class VarUndertow implements Runnable {

	private final static Logger logger = LoggerFactory.getLogger(VarUndertow.class);
	private final CompletableFuture<Boolean> started = new CompletableFuture<>();
	protected VarServlet servlet;

	protected VarConfig varConfig;
	private final Map<String, HttpServlet> servlets = new LinkedHashMap<>();
	private Undertow server;
	private SSLContext sslContext;
	private ExecutorService executorService;
	private GracefulShutdownHandler gracefulShutdown;

	@Inject
	public VarUndertow(VarConfig varConfig, Provider<ParameterHandler> parameterHandlerProvider, ControllerMapper controllerMapper,
					   ObjectFactory objectFactory, ControllerFilter controllerFilter) {
		this.varConfig = varConfig;
		this.servlet = new VarServlet(varConfig, parameterHandlerProvider.get(), controllerMapper, objectFactory, controllerFilter);
		servlets.put("/", servlet);
	}

	public void configure(Consumer<VarConfiguration> configuration) {
		servlet.configure(c -> {
			c.addParameterHandler(VarWebSocketHandler.class);
			configuration.accept(c);
		});
	}

	public void registerServlet(String path, HttpServlet servlet) {
		servlets.put(path, servlet);
	}

	@Override
	public void run() {
		try {
			DeploymentInfo servletBuilder = Servlets.deployment()
					.setClassLoader(VarUndertow.class.getClassLoader())
					.setContextPath("/");
			for (Map.Entry<String, HttpServlet> servlet : servlets.entrySet()) {

				ServletInfo servletInfo = Servlets.servlet(servlet.getKey(), servlet.getValue().getClass(), () -> new InstanceHandle<>() {
					@Override
					public Servlet getInstance() {
						return servlet.getValue();
					}

					@Override
					public void release() {
						servlet.getValue().destroy();
					}
				}).addMapping(servlet.getKey());

				servletBuilder.addServlets(servletInfo);
				servletBuilder.setDeploymentName("Var deployment");
			}


			DeploymentManager manager = Servlets.defaultContainer().addDeployment(servletBuilder);

			manager.deploy();
			PathHandler path = Handlers.path(Handlers.redirect("/"));

//            path.addExactPath("/api/socketInit", websocket(webSocketHandler));
			path.addPrefixPath("/", manager.start());

			Undertow.Builder builder = Undertow.builder()
					.setHandler(this.gracefulShutdown = new GracefulShutdownHandler(path));

			if (sslContext != null) {
				builder.addHttpsListener(varConfig.getPort(), "localhost", sslContext);
			} else {
				builder.addHttpListener(varConfig.getPort(), "localhost");
			}

			if (this.executorService != null) {
				XnioWorker.Builder workerBuilder = Xnio.getInstance().createWorkerBuilder();
				workerBuilder.setExternalExecutorService(executorService);
				builder.setWorker(workerBuilder.build());
			}

			server = builder.build();

			server.start();

			started.complete(true);
			logger.info("var-http started");
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	public void stop(Duration awaitTimeout) {
		gracefulShutdown.shutdown();
		try {
			gracefulShutdown.awaitShutdown(awaitTimeout.toMillis());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException(e);
		} finally {
			server.stop();
		}
		servlets.values().forEach(HttpServlet::destroy);
	}

	public void setSslContext(InputStream x509Certificate, InputStream privateKey) {
		try {
			// Initialise the keystore
			char[] password = "".toCharArray();
			KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
			ks.load(null, password);
			java.security.cert.Certificate certificate = CertificateFactory.getInstance("X.509")
					.generateCertificate(x509Certificate);

			String text = new BufferedReader(
					new InputStreamReader(privateKey, StandardCharsets.UTF_8))
					.lines()
					.collect(Collectors.joining("\n"));

			text = text.replaceAll("-----(BEGIN|END) PRIVATE KEY-----", "").replace("\n", "");
			text = text.replaceAll("", "");
			byte[] dec = Base64.getDecoder().decode(text);

			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(dec);

			PrivateKey privKey = KeyFactory.getInstance("RSA").generatePrivate(keySpec);


			ks.setCertificateEntry("server", certificate);
			ks.setKeyEntry("server", privKey, "".toCharArray(), new Certificate[]{certificate});


			setSslContext(ks, password);
			// Set up the key manager factory
		} catch (NoSuchAlgorithmException | KeyStoreException | CertificateException | IOException |
				 InvalidKeySpecException e) {
			throw new VarInitializationException(e);
		}
	}

	public void setSslContext(KeyStore keyStore, char[] password) {
		try {
			// Set up the key manager factory
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(keyStore, password);

			// Set up the trust manager factory
			TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
			tmf.init(keyStore);

			// Set up the HTTPS context and parameters
			sslContext = SSLContext.getInstance("TLS");
			sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
		} catch (NoSuchAlgorithmException | KeyStoreException | UnrecoverableKeyException | KeyManagementException e) {
			throw new VarInitializationException(e);
		}
	}

	public VarServlet getServlet() {
		return servlet;
	}

	public CompletableFuture<Boolean> getStarted() {
		return started;
	}

	public void setExecutor(ExecutorService executorService) {
		this.executorService = executorService;
	}
}

