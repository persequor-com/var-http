package io.varhttp.parameterhandlers;

import io.varhttp.RequestHeader;
import io.varhttp.VarRequestHeader;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class RequestHeaderParameterHandler implements IParameterHandlerMatcher {
	@Override
	public int getPriority() {
		return 80;
	}

	@Override
	public IParameterHandler getHandlerIfMatches(Method method, Parameter parameter, String path, String classPath) {
		if (RequestHeader.class == parameter.getType()) {
			return  context -> new VarRequestHeader(context.request());
		}
		return null;
	}
}
