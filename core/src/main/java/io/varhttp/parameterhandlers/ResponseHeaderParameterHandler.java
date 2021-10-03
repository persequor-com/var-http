package io.varhttp.parameterhandlers;

import io.varhttp.ResponseHeader;
import io.varhttp.VarResponseHeader;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class ResponseHeaderParameterHandler implements IParameterHandlerMatcher {
	@Override
	public int getPriority() {
		return 90;
	}

	@Override
	public IParameterHandler getHandlerIfMatches(Method method, Parameter parameter, String path, String classPath) {
		if (ResponseHeader.class == parameter.getType()) {
			return context -> new VarResponseHeader(context, classPath);
		}
		return null;
	}
}
