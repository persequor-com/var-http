package io.varhttp.filterorder;

import io.varhttp.controllers.withfilters.FilterCatcher;

import javax.inject.Inject;

public class ShouldNotBeRunFilterOnTheSide extends OrderFilter {
	@Inject
	public ShouldNotBeRunFilterOnTheSide(FilterCatcher filterCatcher) {
		super(filterCatcher);
		order = getClass().getSimpleName();
	}
}
