package io.varhttp.filterorder;

import io.varhttp.Controller;
import io.varhttp.ControllerClass;
import io.varhttp.HttpMethod;
import io.varhttp.controllers.withfilters.FilterCatcher;

import javax.inject.Inject;

@ClassFilter1
@ClassFilter2
@OverridingFilter
@ControllerClass
public class FilterControllerClass {
	private FilterCatcher filterCatcher;

	@Inject
	public FilterControllerClass(FilterCatcher filterCatcher) {
		this.filterCatcher = filterCatcher;
	}

	@Controller(path = "/filter-order", httpMethods = HttpMethod.GET)
	@MethodFilter1
	@MethodFilter2
	@OverridingFilter
	public void filterOrder() {
		filterCatcher.add("controller");
	}
}
