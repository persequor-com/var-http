package io.varhttp;

import com.google.common.base.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

@Singleton
public class VarServlet extends HttpServlet {
	private final BaseVarConfigurationContext baseConfigurationContext;
	private final Logger logger = LoggerFactory.getLogger(VarServlet.class);
	private final VarConfig varConfig;
	private final ParameterHandler parameterHandler;
	final ExecutionMap executions;
	private ControllerMapper controllerMapper;
	private HashMap<String, String> redirects = new HashMap<>();
	private List<VarWebSocket> webSockets = new ArrayList<>();
	private final RegisteredWebSockets registeredWebSockets;

	public VarServlet(VarConfig varConfig, ParameterHandler parameterHandler, ControllerMapper controllerMapper, ObjectFactory objectFactory, ControllerFilter controllerFilter, RegisteredWebSockets registeredWebSockets, IWebSocketProvider webSocketProvider) {
		this.varConfig = varConfig;
		this.parameterHandler = parameterHandler;
		this.controllerMapper = controllerMapper;
		this.registeredWebSockets = registeredWebSockets;

		this.baseConfigurationContext = new BaseVarConfigurationContext(this, this.parameterHandler, objectFactory, controllerFilter, registeredWebSockets, webSocketProvider);
		this.executions = new ExecutionMap(this.baseConfigurationContext);
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		handle(request, response);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		handle(request, response);
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
		handle(req, resp);
	}

	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse resp) {
		handle(req, resp);
	}

	@Override
	protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
		handle(req, resp);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
		handle(req, resp);
	}

	@Override
	protected void doTrace(HttpServletRequest req, HttpServletResponse resp) {
		handle(req, resp);
	}

	public void handle(HttpServletRequest request, HttpServletResponse response) {
		lockDefaultEncoding(request);
		final HttpMethod httpMethod = HttpMethod.valueOf(request.getMethod());
		String servletPath = request.getRequestURI();
		if (servletPath.contains("?")) {
			servletPath = servletPath.substring(0, servletPath.indexOf("?"));
		}
		Request r = new Request(httpMethod, servletPath);

		final String[] requestPath;
		if (redirects.containsKey(r.path)) {
			requestPath = redirects.get(r.path).substring(1).split("/");
		} else {
			requestPath = r.path.substring(1).split("/");
		}

		ControllerExecution exe = null;

		exe = executions.get(requestPath, r.method);

		if (exe != null) {
			try {
				exe.execute(new ControllerContext(request, response, varConfig));
			} catch (Exception e) {
				logger.error("Execution failed: " + e.getMessage(), e);
				response.setStatus(500);
				return;
			}
		} else {
			response.setStatus(404);
			return;
		}

		try {
			if (!"head".equalsIgnoreCase(request.getMethod())) {
				response.getOutputStream().flush();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static void lockDefaultEncoding(HttpServletRequest request) {
		String characterEncoding = request.getCharacterEncoding();
		if(characterEncoding == null){
			try {
				request.setCharacterEncoding(Charsets.UTF_8.toString());
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void configure(Consumer<VarConfiguration> configuration) {
		VarConfiguration varConfiguration = new VarConfiguration(this, controllerMapper, baseConfigurationContext, parameterHandler, registeredWebSockets, null);
		configuration.accept(varConfiguration);
		baseConfigurationContext.applyMappings();
		varConfiguration.applyMappings();
	}

	public void redirect(String from, String to) {
		redirects.put(from, to);
	}

	@Override
	public void destroy() {
		super.destroy();
		for (VarWebSocket webSocket : webSockets) {
			try {
				webSocket.close();
			} catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}
	}
}
