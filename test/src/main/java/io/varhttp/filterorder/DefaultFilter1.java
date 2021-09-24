package io.varhttp.filterorder;

import io.varhttp.controllers.withfilters.FilterCatcher;

import javax.inject.Inject;

public class DefaultFilter1 extends OrderFilter {
	@Inject
	public DefaultFilter1(FilterCatcher filterCatcher) {
		super(filterCatcher);
		order = getClass().getSimpleName();
	}
}
