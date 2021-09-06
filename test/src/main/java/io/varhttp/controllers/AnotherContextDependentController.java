package io.varhttp.controllers;

import io.varhttp.Controller;
import io.varhttp.ControllerClass;

import javax.inject.Inject;

@ControllerClass
public class AnotherContextDependentController {
	private IMyContext myContext;

	@Inject
	public AnotherContextDependentController(IMyContext myContext) {
		this.myContext = myContext;
	}

	@Controller(path = "/anothercontextdependent")
	public String contextdependent() {
		return myContext.contextResult();
	}
}
