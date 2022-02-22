package io.varhttp.controllers.withfilters;

import io.varhttp.FilterMethod;
import io.varhttp.VarFilterChain;

import javax.inject.Inject;

public class Filter2 {
	private FilterCatcher filterCatcher;

	@Inject
	public Filter2(FilterCatcher filterCatcher) {
		this.filterCatcher = filterCatcher;
	}

	@FilterMethod
	public void filter(VarFilterChain chain) throws Exception {
		filterCatcher.add("Filter2 before proceed");
		chain.proceed();
		filterCatcher.add("Filter2 after proceed");
	}
}
