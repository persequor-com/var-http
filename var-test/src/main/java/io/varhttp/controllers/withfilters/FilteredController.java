package io.varhttp.controllers.withfilters;

import io.varhttp.Controller;
import io.varhttp.ControllerClass;
import io.varhttp.Filter;
import io.varhttp.HttpMethod;

import javax.inject.Inject;

@Authentication
@Authorization(Role.Admin)
@ControllerClass
public class FilteredController {
	private FilterCatcher filterCatcher;

	@Inject
	public FilteredController(FilterCatcher filterCatcher) {
		this.filterCatcher = filterCatcher;
	}

	@Controller(path = "/muh", httpMethods = {HttpMethod.GET})
	@Filter(Filter1.class)
	@Filter(Filter2.class)
	public void muh() {
		filterCatcher.add("Muh was called");
	}

	@Controller(path = "/login", httpMethods = {HttpMethod.GET})
	@Authorization(Role.None)
	public void login() {
		filterCatcher.add("Login was called");
	}
}
