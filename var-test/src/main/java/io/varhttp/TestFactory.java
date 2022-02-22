package io.varhttp;

import io.odinjector.Injector;

import javax.inject.Inject;

public class TestFactory implements ObjectFactory {
	private Injector injector;

	@Inject
	public TestFactory(Injector injector) {
		this.injector = injector;
	}

	@Override
	public <T> T getInstance(Class<T> controllerClass) {
		return injector.getInstance(controllerClass);
	}
}
