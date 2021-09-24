package io.varhttp.filterorder;

import io.varhttp.controllers.withfilters.FilterCatcher;

import javax.inject.Inject;

public class DefaultFilter4 extends OrderFilter {
	@Inject
	public DefaultFilter4(FilterCatcher filterCatcher) {
		super(filterCatcher);
		order = getClass().getSimpleName();
	}
}
