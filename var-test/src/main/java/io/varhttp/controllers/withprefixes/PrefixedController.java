package io.varhttp.controllers.withprefixes;

import io.varhttp.Controller;
import io.varhttp.ControllerClass;

@ControllerClass(pathPrefix = "/classprefix")
public class PrefixedController {
	@Controller(path = "/controller")
	public String prefixed() {
		return "prefixed";
	}
}
