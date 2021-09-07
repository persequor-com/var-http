	package io.varhttp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.function.Consumer;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class VarServlet extends HttpServlet {
	private final BaseVarConfigurationContext baseConfigurationContext;
	Logger logger = LoggerFactory.getLogger(VarServlet.class);
	private final ParameterHandler parameterHandler;
	final ExecutionMap executions;


	private VarConfig varConfig;
	private ControllerMapper controllerMapper;

	public VarServlet(ParameterHandler parameterHandler, VarConfig varConfig, ControllerMapper controllerMapper, FilterFactory filterFactory, ControllerFactory controllerFactory, ControllerFilter controllerFilter) {
		this.parameterHandler = parameterHandler;
		this.varConfig = varConfig;
		this.controllerMapper = controllerMapper;
		this.executions = new ExecutionMap();
		this.baseConfigurationContext = new BaseVarConfigurationContext(this, this.parameterHandler, filterFactory, controllerFactory, controllerFilter);
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			handle(request, response);
		} catch (Throwable e) {
			System.out.println(e.toString());
			throw e;
		}
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		handle(request, response);
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		handle(req, resp);
	}

	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		handle(req, resp);
	}

	@Override
	protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		handle(req, resp);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		handle(req, resp);
	}

	@Override
	protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		handle(req, resp);
	}

	public void handle(HttpServletRequest request, HttpServletResponse response) {
		final HttpMethod httpMethod = HttpMethod.valueOf(request.getMethod());
		String servletPath = request.getRequestURI();
		if (servletPath.contains("?")) {
			servletPath = servletPath.substring(0, servletPath.indexOf("?"));
		}
		Request r = new Request(httpMethod, servletPath);

		ControllerExecution exe = null;

		exe = executions.get(r.path.substring(1).split("/"), r.method);

		if (exe != null) {
			try {
				exe.execute(new ControllerContext(request, response));
			} catch (Exception e) {
				logger.error("Execution failed: "+e.getMessage(), e);
				response.setStatus(500);
				return;
			}
		} else {
			// Strange error message
			response.setStatus(404);
			return;
		}

		try {
			if (!"head".equals(request.getMethod().toLowerCase())) {
				response.getOutputStream().flush();
			}
			response.setStatus(200);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void configure(Consumer<VarConfiguration> configuration) {
		configuration.accept(new VarConfiguration(this, controllerMapper, baseConfigurationContext, parameterHandler));
	}

}
