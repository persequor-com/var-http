package io.varhttp.parameterhandlers;

import io.varhttp.ControllerContext;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class HttpServletRequestParameterHandler implements IParameterHandlerMatcher {
	@Override
	public int getPriority() {
		return 120;
	}

	@Override
	public IParameterHandler getHandlerIfMatches(Method method, Parameter parameter, String path, String classPath) {
		if (HttpServletRequest.class == parameter.getType()) {
			return ControllerContext::request;
		}

		return null;
	}
}
