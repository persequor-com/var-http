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
	public Filter getInstance(Class<? extends Filter> filterClass) {
		return injector.getInstance(filterClass);
	}
}
