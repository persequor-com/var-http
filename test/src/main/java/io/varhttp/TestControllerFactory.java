package io.varhttp;

import io.odinjector.Injector;

import javax.inject.Inject;

public class TestControllerFactory implements ControllerFactory {
	private Injector injector;

	@Inject
	public TestControllerFactory(Injector injector) {
		this.injector = injector;
	}

	@Override
	public Object getInstance(Class<?> controllerClass) {
		return injector.getInstance(controllerClass);
	}
}
