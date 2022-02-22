package io.varhttp.parameterhandlers;

import io.varhttp.ControllerContext;
import io.varhttp.RequestParameters;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class RequestParametersHandler implements IParameterHandlerMatcher {
	@Override
	public int getPriority() {
		return 110;
	}

	@Override
	public IParameterHandler getHandlerIfMatches(Method method, Parameter parameter, String path, String classPath) {
		if (RequestParameters.class == parameter.getType()) {
			return ControllerContext::getParameters;
		}
		return null;
	}
}
