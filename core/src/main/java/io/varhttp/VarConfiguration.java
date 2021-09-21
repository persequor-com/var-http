package io.varhttp;

import io.varhttp.parameterhandlers.IParameterHandlerMatcher;

import javax.servlet.Filter;
import java.util.function.Consumer;

public class VarConfiguration {
	private VarServlet servlet = null;
	private final VarConfigurationContext context;
	private ControllerMapper controllerMapper = null;
	private ParameterHandler parameterHandler;
	private ExceptionRegistry exceptionRegistry;

	public VarConfiguration(VarServlet servlet, ControllerMapper controllerMapper, VarConfigurationContext context, ParameterHandler parameterHandler) {
		this.servlet = servlet;
		this.controllerMapper = controllerMapper;
		this.context = context;
		this.parameterHandler = parameterHandler;
	}

	public void addControllerPackage(Package controllerPackage) {
		controllerMapper.map(context, controllerPackage.getName());
	}

	public void addController(Class<?> controller) {
		controllerMapper.map(context, controller);
	}

	public void setControllerFactory(ControllerFactory controllerFactory) {
		this.context.setControllerFactory(controllerFactory);
	}

	public void addParameterHandler(Class<? extends IParameterHandlerMatcher> handlerMatcher) {
		context.addParameterHandler(handlerMatcher);
	}

	public void addDefaultFilter(Class<? extends Filter> filter) {
		context.addDefaultFilter(filter);
	}

	public void addControllerMatcher(ControllerMatcher controllerMatcher) {
		context.addControllerMatcher(controllerMatcher);
	}

	public void addExceptionMapper(ControllerExceptionMapper controllerExceptionMapper) {
		context.getExceptionRegistry().registerException(controllerExceptionMapper);
	}

	public void setBasePath(String basePath) {
		context.setBasePath(basePath);
	}

	public void configure(Consumer<VarConfiguration> configuration) {
		configuration.accept(new VarConfiguration(servlet, controllerMapper, new VarConfigurationContext(servlet, context, parameterHandler), parameterHandler));
	}
}
