package io.varhttp.controllers.withfilters;

import io.varhttp.FilterMethod;
import io.varhttp.VarFilterChain;

import javax.inject.Inject;

public class Filter1 {
	private FilterCatcher filterCatcher;

	@Inject
	public Filter1(FilterCatcher filterCatcher) {
		this.filterCatcher = filterCatcher;
	}

	@FilterMethod
	public void filter() throws Exception {
		// Not depending on chain means proceed is called automatically
		filterCatcher.add("Filter 1 was called");
	}
}
