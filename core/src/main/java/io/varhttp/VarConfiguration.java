package io.varhttp;

import io.varhttp.parameterhandlers.IParameterHandlerMatcher;

import javax.servlet.Filter;
import java.lang.reflect.Method;
import java.util.function.Consumer;

public class VarConfiguration {
	private VarServlet servlet = null;
	private final VarConfigurationContext context;
	private ControllerMapper controllerMapper = null;
	private final ParameterHandler parameterHandler;

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

	public void addDefaultVarFilter(Class<?> filterClass) {
		context.addDefaultVarFilter(filterClass);
	}

	public void addDefaultVarFilter(Class<?> filterClass, Method method) {
		context.addDefaultVarFilter(filterClass, method);
	}

	public void setNotFoundController(Class<?> filterClass) {
		context.setNotFoundController(filterClass);
	}

	public void setNotFoundController(Class<?> filterClass, Method method) {
		context.setNotFoundController(filterClass, method);
	}

	public void addControllerMatcher(ControllerMatcher controllerMatcher) {
		context.addControllerMatcher(controllerMatcher);
	}

	public void setBasePath(String basePath) {
		context.setBasePath(basePath);
		servlet.executions.createPathContext(context, basePath);
	}

	public void configure(Consumer<VarConfiguration> configuration) {
		configuration.accept(new VarConfiguration(servlet, controllerMapper, new VarConfigurationContext(servlet, context, parameterHandler), parameterHandler));
	}
}
