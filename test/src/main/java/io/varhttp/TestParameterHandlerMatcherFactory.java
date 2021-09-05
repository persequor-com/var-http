package io.varhttp;

import io.odinjector.Injector;
import io.varhttp.parameterhandlers.IParameterHandlerMatcher;

import javax.inject.Inject;

public class TestParameterHandlerMatcherFactory implements ParameterHandlerMatcherFactory {
	private Injector injector;

	@Inject
	public TestParameterHandlerMatcherFactory(Injector injector) {
		this.injector = injector;
	}

	@Override
	public IParameterHandlerMatcher get(Class<? extends IParameterHandlerMatcher> clazz) {
		return injector.getInstance(clazz);
	}
}
