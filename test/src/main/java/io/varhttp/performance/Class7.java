package io.varhttp.performance;

import io.varhttp.Controller;
import io.varhttp.ControllerClass;
import io.varhttp.controllers.withfilters.Authentication;
import io.varhttp.controllers.withfilters.Authorization;
import io.varhttp.controllers.withfilters.FilterCatcher;
import io.varhttp.controllers.withfilters.Role;

import javax.inject.Inject;
import javax.inject.Singleton;

@Authentication
@Authorization(Role.Admin)
@ControllerClass
@Singleton
public class Class7 {
	private FilterCatcher filterCatcher;

	@Inject
	public Class7(FilterCatcher filterCatcher) {
		this.filterCatcher = filterCatcher;
	}

	@Controller(path = "/class7/controller1")
	public void c1() {
		filterCatcher.add("Muh was called");
	}

	@Controller(path = "/class7/controller2")
	@Authorization(Role.None)
	public void c2() {
		filterCatcher.add("Login was called");
	}

	@Controller(path = "/class7/controller3")
	public void c3() {
		filterCatcher.add("Muh was called");
	}

	@Controller(path = "/class7/controller4")
	public void c4() {
		filterCatcher.add("Muh was called");
	}

	@Controller(path = "/class7/controller5")
	public void c5() {
		filterCatcher.add("Muh was called");
	}
}
