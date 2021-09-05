package io.varhttp;

import io.varhttp.parameterhandlers.IParameterHandlerMatcher;

import javax.servlet.Filter;

public class VarConfiguration {
	private final VarServlet servlet;
	private final ControllerMapper controllerMapper;
	private final VarConfig varConfig;

	public VarConfiguration(VarServlet servlet, ControllerMapper controllerMapper, VarConfig varConfig) {
		this.servlet = servlet;
		this.controllerMapper = controllerMapper;
		this.varConfig = varConfig;
	}

	public void addControllerPackage(Package controllerPackage) {
		controllerMapper.map(servlet, controllerPackage.getName());
	}

	public void addController(Class<?> controller) {
		controllerMapper.map(servlet, controller);
	}

	public void addParameterHandler(Class<? extends IParameterHandlerMatcher> handlerMatcher) {
		servlet.addParameterHandler(handlerMatcher);
	}

	public void addDefaultFilter(Class<? extends Filter> filter) {
		servlet.addDefaultFilter(filter);
	}
}
