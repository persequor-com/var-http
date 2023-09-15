package io.varhttp.test;

import io.varhttp.Controller;
import io.varhttp.ControllerClass;
import io.varhttp.HttpMethod;

@ControllerClass
public class ControllerInTestPackage {
	@Controller(path = "/controller-in-test", httpMethods = {HttpMethod.GET})
	public void controller() {

	}

}
