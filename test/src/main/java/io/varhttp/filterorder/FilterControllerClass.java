package io.varhttp.filterorder;

import io.varhttp.Controller;
import io.varhttp.ControllerClass;
import io.varhttp.HttpMethod;
import io.varhttp.controllers.withfilters.FilterCatcher;

import javax.inject.Inject;

@ClassFilter1(20)
@ClassFilter2(21)
@OverridingFilter(22)
@ControllerClass
public class FilterControllerClass {
	private FilterCatcher filterCatcher;

	@Inject
	public FilterControllerClass(FilterCatcher filterCatcher) {
		this.filterCatcher = filterCatcher;
	}

	@Controller(path = "/filter-order", httpMethods = HttpMethod.GET)
	@MethodFilter1(30)
	@MethodFilter2(31)
	@OverridingFilter(32)
	public void filterOrder() {
		filterCatcher.add("controller");
	}
}
