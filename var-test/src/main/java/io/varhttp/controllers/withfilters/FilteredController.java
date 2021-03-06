package io.varhttp.controllers.withfilters;

import io.varhttp.Controller;
import io.varhttp.ControllerClass;
import io.varhttp.Filter;

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

	@Controller(path = "/muh")
	@Filter(Filter1.class)
	@Filter(Filter2.class)
	public void muh() {
		filterCatcher.add("Muh was called");
	}

	@Controller(path = "/login")
	@Authorization(Role.None)
	public void login() {
		filterCatcher.add("Login was called");
	}
}
