package io.varhttp.parameterhandlers;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public interface IParameterHandlerMatcher extends Comparable {
	@Override
	default int compareTo(Object o) {
		return getPriority() - ((IParameterHandlerMatcher)o).getPriority();
	}

	int getPriority();
	IParameterHandler getHandlerIfMatches(Method method, Parameter parameter, String path, String classPath);
}
