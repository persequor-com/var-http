package io.varhttp.test.sub;

import io.varhttp.Controller;
import io.varhttp.ControllerClass;

@ControllerClass
public class ControllerInSub {
	@Controller(path = "/controller-in-sub")
	public void controller() {

	}
}
