package io.varhttp;

import io.odinjector.Injector;

import javax.inject.Inject;
import javax.servlet.Filter;

public class TestFilterFactory implements FilterFactory {
	private Injector injector;

	@Inject
	public TestFilterFactory(Injector injector) {
		this.injector = injector;
	}

	@Override
	public Object getInstance(Class<?> filterClass) {
		return injector.getInstance(filterClass);
	}
}
