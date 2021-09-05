package io.varhttp.parameterhandlers;

import io.varhttp.ControllerContext;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class HttpServletResponseParameterHandler implements IParameterHandlerMatcher {
	@Override
	public int getPriority() {
		return 130;
	}

	@Override
	public IParameterHandler getHandlerIfMatches(Method method, Parameter parameter, String path, String classPath) {
		if (HttpServletResponse.class == parameter.getType()) {
			return ControllerContext::response;
		}

		return null;
	}
}
