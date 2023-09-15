package io.varhttp.performance;

import io.varhttp.*;
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
public class Class4 {
	private FilterCatcher filterCatcher;

	@Inject
	public Class4(FilterCatcher filterCatcher) {
		this.filterCatcher = filterCatcher;
	}
	@Controller(path = "/class4/controller1/{muh}", httpMethods = {HttpMethod.GET})
	public String c1(@RequestParameter(name = "name") String name, @PathVariable(name = "muh") String path, @RequestBody String body) {
		filterCatcher.add("Muh was called");
		return "muh";
	}

	@Controller(path = "/class4/controller2/{muh}", httpMethods = {HttpMethod.GET})
	@Authorization(Role.None)
	public String c2(@RequestParameter(name = "name") String name, @PathVariable(name = "muh") String path, @RequestBody String body) {
		filterCatcher.add("Login was called");
		return "muh";
	}

	@Controller(path = "/class4/controller3/{muh}", httpMethods = {HttpMethod.GET})
	public String c3(@RequestParameter(name = "name") String name, @PathVariable(name = "muh") String path, @RequestBody String body) {
		filterCatcher.add("Muh was called");
		return "muh";
	}

	@Controller(path = "/class4/controller4/{muh}", httpMethods = {HttpMethod.GET})
	public String c4(@RequestParameter(name = "name") String name, @PathVariable(name = "muh") String path, @RequestBody String body) {
		filterCatcher.add("Muh was called");
		return "muh";
	}

	@Controller(path = "/class4/controller5/{muh}", httpMethods = {HttpMethod.GET})
	public String c5(@RequestParameter(name = "name") String name, @PathVariable(name = "muh") String path, @RequestBody String body) {
		filterCatcher.add("Muh was called");
		return "muh";
	}
}
