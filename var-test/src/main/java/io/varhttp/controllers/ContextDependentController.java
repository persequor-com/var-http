package io.varhttp.controllers;

import io.varhttp.Controller;
import io.varhttp.ControllerClass;

import javax.inject.Inject;

@ControllerClass
public class ContextDependentController {
	private IMyContext myContext;

	@Inject
	public ContextDependentController(IMyContext myContext) {
		this.myContext = myContext;
	}

	@Controller(path = "/contextdependent")
	public String contextdependent() {
		return myContext.contextResult();
	}
}
