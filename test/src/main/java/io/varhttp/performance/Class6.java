package io.varhttp.performance;

import io.varhttp.Controller;
import io.varhttp.ControllerClass;
import io.varhttp.PathVariable;
import io.varhttp.RequestBody;
import io.varhttp.RequestParameter;
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
public class Class6 {
	private FilterCatcher filterCatcher;
	@Controller(path = "/class6/controller1/{muh}")
	public String c1(@RequestParameter(name = "name") String name, @PathVariable(name = "muh") String path, @RequestBody String body) {
		filterCatcher.add("Muh was called");
		return "muh";
	}

	@Controller(path = "/class6/controller2/{muh}")
	@Authorization(Role.None)
	public String c2(@RequestParameter(name = "name") String name, @PathVariable(name = "muh") String path, @RequestBody String body) {
		filterCatcher.add("Login was called");
		return "muh";
	}

	@Controller(path = "/class6/controller3/{muh}")
	public String c3(@RequestParameter(name = "name") String name, @PathVariable(name = "muh") String path, @RequestBody String body) {
		filterCatcher.add("Muh was called");
		return "muh";
	}

	@Controller(path = "/class6/controller4/{muh}")
	public String c4(@RequestParameter(name = "name") String name, @PathVariable(name = "muh") String path, @RequestBody String body) {
		filterCatcher.add("Muh was called");
		return "muh";
	}

	@Controller(path = "/class6/controller5/{muh}")
	public String c5(@RequestParameter(name = "name") String name, @PathVariable(name = "muh") String path, @RequestBody String body) {
		filterCatcher.add("Muh was called");
		return "muh";
	}
}
