package io.varhttp.filterorder;

import io.varhttp.controllers.withfilters.FilterCatcher;

import javax.inject.Inject;

public class DefaultFilter3 extends OrderFilter {
	@Inject
	public DefaultFilter3(FilterCatcher filterCatcher) {
		super(filterCatcher);
		order = getClass().getSimpleName();
	}
}
