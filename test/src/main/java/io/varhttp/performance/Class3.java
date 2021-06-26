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
public class Class3 {
	private FilterCatcher filterCatcher;

	@Inject
	public Class3(FilterCatcher filterCatcher) {
		this.filterCatcher = filterCatcher;
	}

	@Controller(path = "/class3/controller1")
	public String c1() {
		filterCatcher.add("Muh was called");
		return "muh";
	}

	@Controller(path = "/class3/controller2")
	@Authorization(Role.None)
	public String c2() {
		filterCatcher.add("Login was called");
		return "muh";
	}

	@Controller(path = "/class3/controller3")
	public String c3() {
		filterCatcher.add("Muh was called");
		return "muh";
	}

	@Controller(path = "/class3/controller4")
	public String c4() {
		filterCatcher.add("Muh was called");
		return "muh";
	}

	@Controller(path = "/class3/controller5")
	public String c5() {
		filterCatcher.add("Muh was called");
		return "muh";
	}
}
