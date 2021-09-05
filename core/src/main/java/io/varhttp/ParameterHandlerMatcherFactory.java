package io.varhttp;

import io.varhttp.parameterhandlers.IParameterHandlerMatcher;

public interface ParameterHandlerMatcherFactory {
	IParameterHandlerMatcher get(Class<? extends IParameterHandlerMatcher> clazz);
}
