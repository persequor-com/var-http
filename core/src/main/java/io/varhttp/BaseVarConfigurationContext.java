package io.varhttp;

import io.varhttp.parameterhandlers.HttpServletRequestParameterHandler;
import io.varhttp.parameterhandlers.HttpServletResponseParameterHandler;
import io.varhttp.parameterhandlers.PathVariableParameterHandlerMatcher;
import io.varhttp.parameterhandlers.RequestBodyHandlerMatcher;
import io.varhttp.parameterhandlers.RequestHeaderParameterHandler;
import io.varhttp.parameterhandlers.RequestParameterHandlerMatcher;
import io.varhttp.parameterhandlers.RequestParametersHandler;
import io.varhttp.parameterhandlers.ResponseHeaderParameterHandler;
import io.varhttp.parameterhandlers.ResponseStreamParameterHandler;

import javax.inject.Inject;
import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class BaseVarConfigurationContext extends VarConfigurationContext {

	@Inject
	public BaseVarConfigurationContext(VarServlet varServlet, ParameterHandler parameterHandler, FilterFactory filterFactory, ControllerFactory controllerFactory, ControllerFilter controllerFilter) {
		super(varServlet, null, parameterHandler);
		this.filterFactory = filterFactory;
		this.parameterHandler = parameterHandler;
		this.controllerFactory = controllerFactory;
		this.controllerFilter = controllerFilter;
		parameterHandler.addParameterHandler(ResponseStreamParameterHandler.class);
		parameterHandler.addParameterHandler(ResponseHeaderParameterHandler.class);
		parameterHandler.addParameterHandler(RequestParametersHandler.class);
		parameterHandler.addParameterHandler(RequestParameterHandlerMatcher.class);
		parameterHandler.addParameterHandler(RequestHeaderParameterHandler.class);
		parameterHandler.addParameterHandler(RequestBodyHandlerMatcher.class);
		parameterHandler.addParameterHandler(PathVariableParameterHandlerMatcher.class);
		parameterHandler.addParameterHandler(HttpServletRequestParameterHandler.class);
		parameterHandler.addParameterHandler(HttpServletResponseParameterHandler.class);
		controllerMatchers.add(new VarControllerMatcher());
		exceptionRegistry = new ExceptionRegistry();
	}

	ParameterHandler getParameterHandler() {
		return parameterHandler;
	}

	FilterFactory getFilterFactory() {
		if (filterFactory == null) {
			throw new VarInitializationException("Your must register a filter factory for var-http to run");
		}
		return filterFactory;
	}

	ControllerFilter getControllerFilter() {
		return controllerFilter;
	}

	ExceptionRegistry getExceptionRegistry() {
		return exceptionRegistry;
	}

	ControllerFactory getControllerFactory() {
		if (filterFactory == null) {
			throw new VarInitializationException("Your must register a controller factory for var-http to run");
		}
		return controllerFactory;
	}

	@Override
	public List<ControllerMatcher> getControllerMatchers() {
		return controllerMatchers;
	}

	@Override
	LinkedList<Object> getDefaultFilters() {
		return defaultFilters;
	}

	@Override
	public ControllerExecution getNotFoundController() {
		return notFoundController;
	}
}
