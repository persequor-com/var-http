package io.varhttp.test;

import io.varhttp.Controller;
import io.varhttp.ControllerClass;

@ControllerClass
public class ControllerInTestPackage {
	@Controller(path = "/controller-in-test")
	public void controller() {

	}

}
