package io.varhttp;

import io.varhttp.parameterhandlers.IParameterHandlerMatcher;

import javax.servlet.Filter;

public class VarConfiguration {
	private final VarServlet servlet;
	private final ControllerMapper controllerMapper;
	private final VarConfig varConfig;
	private ControllerFactory controllerFactory;
	private ExceptionRegistry exceptionRegistry;

	public VarConfiguration(VarServlet servlet, ControllerMapper controllerMapper, VarConfig varConfig, ControllerFactory controllerFactory, ExceptionRegistry exceptionRegistry) {
		this.servlet = servlet;
		this.controllerMapper = controllerMapper;
		this.varConfig = varConfig;
		this.controllerFactory = controllerFactory;
		this.exceptionRegistry = exceptionRegistry;
	}

	public void addControllerPackage(Package controllerPackage) {
		controllerMapper.map(servlet, controllerPackage.getName(), controllerFactory);
	}

	public void addController(Class<?> controller) {
		controllerMapper.map(servlet, controller, controllerFactory);
	}

	public void setControllerFactory(ControllerFactory controllerFactory) {
		this.controllerFactory = controllerFactory;
	}

	public void addParameterHandler(Class<? extends IParameterHandlerMatcher> handlerMatcher) {
		servlet.addParameterHandler(handlerMatcher);
	}

	public void addDefaultFilter(Class<? extends Filter> filter) {
		servlet.addDefaultFilter(filter);
	}

	public void addControllerMatcher(ControllerMatcher controllerMatcher) {
		servlet.addControllerMatcher(controllerMatcher);
	}
}
