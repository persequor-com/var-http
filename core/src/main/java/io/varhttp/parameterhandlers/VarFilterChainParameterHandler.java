package io.varhttp.parameterhandlers;

import io.varhttp.ControllerContext;
import io.varhttp.VarFilterChain;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class VarFilterChainParameterHandler implements IParameterHandlerMatcher {
	@Override
	public int getPriority() {
		return 5;
	}

	@Override
	public IParameterHandler getHandlerIfMatches(Method method, Parameter parameter, String path, String classPath) {
		if (parameter.getType() == VarFilterChain.class) {
			return ControllerContext::getFilterChain;
		}
		return null;
	}
}
