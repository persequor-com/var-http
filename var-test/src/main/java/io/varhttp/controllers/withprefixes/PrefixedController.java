package io.varhttp.controllers.withprefixes;

import io.varhttp.Controller;
import io.varhttp.ControllerClass;
import io.varhttp.HttpMethod;

@ControllerClass(pathPrefix = "/classprefix")
public class PrefixedController {
	@Controller(path = "/controller", httpMethods = {HttpMethod.GET})
	public String prefixed() {
		return "prefixed";
	}
}
