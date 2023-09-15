package io.varhttp.test.sub;

import io.varhttp.Controller;
import io.varhttp.ControllerClass;
import io.varhttp.HttpMethod;

@ControllerClass
public class ControllerInSub {
	@Controller(path = "/controller-in-sub", httpMethods = {HttpMethod.GET})
	public void controller() {

	}
}
