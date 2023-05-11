package io.varhttp;

import io.varhttp.parameterhandlers.HttpServletRequestParameterHandler;
import io.varhttp.parameterhandlers.HttpServletResponseParameterHandler;
import io.varhttp.parameterhandlers.MissingParamException;
import io.varhttp.parameterhandlers.PathVariableParameterHandlerMatcher;
import io.varhttp.parameterhandlers.RequestBodyHandlerMatcher;
import io.varhttp.parameterhandlers.RequestHeaderParameterHandler;
import io.varhttp.parameterhandlers.RequestParameterHandlerMatcher;
import io.varhttp.parameterhandlers.RequestParametersHandler;
import io.varhttp.parameterhandlers.ResponseHeaderParameterHandler;
import io.varhttp.parameterhandlers.ResponseStreamParameterHandler;
import io.varhttp.parameterhandlers.VarFilterChainParameterHandler;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class BaseVarConfigurationContext extends VarConfigurationContext {


	@Inject
	public BaseVarConfigurationContext(VarServlet varServlet, ParameterHandler parameterHandler, ObjectFactory objectFactory, ControllerFilter controllerFilter) {
		super(varServlet, null, parameterHandler);
		this.parameterHandler = parameterHandler;
		this.objectFactory = objectFactory;
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
		parameterHandler.addParameterHandler(VarFilterChainParameterHandler.class);
		controllerMatchers.add(new VarControllerMatcher());
		controllerMatchers.add(new VarWebSocketMatcher());
		exceptionRegistry = new ExceptionRegistry();
		exceptionRegistry.registerException(new ControllerExceptionMapper(ContentTypeException.class, HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE));
		exceptionRegistry.registerException(new ControllerExceptionMapper(MissingParamException.class, HttpServletResponse.SC_BAD_REQUEST));
	}

	ParameterHandler getParameterHandler() {
		return parameterHandler;
	}

	ControllerFilter getControllerFilter() {
		return controllerFilter;
	}

	ExceptionRegistry getExceptionRegistry() {
		return exceptionRegistry;
	}

	ObjectFactory getObjectFactory() {
		if (objectFactory == null) {
			throw new VarInitializationException("Your must register an object factory for var-http to run");
		}
		return objectFactory;
	}

	@Override
	public List<ControllerMatcher> getControllerMatchers() {
		return controllerMatchers;
	}

	@Override
	List<Class<?>> getDefaultFilters() {
		return defaultFilters;
	}

	@Override
	public ControllerExecution getNotFoundController() {
		return notFoundController;
	}

	@Override
	public ControllerListener getOnControllerAdd() {
		return this.onControllerAdd;
	}
}
