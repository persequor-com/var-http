package io.varhttp.filterorder;

import io.varhttp.controllers.withfilters.FilterCatcher;

import javax.inject.Inject;

public class ShouldNotBeRunFilterInner extends OrderFilter {
	@Inject
	public ShouldNotBeRunFilterInner(FilterCatcher filterCatcher) {
		super(filterCatcher);
		order = getClass().getSimpleName();
	}
}
