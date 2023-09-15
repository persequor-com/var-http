package io.varhttp.controllers;

import io.varhttp.Controller;
import io.varhttp.ControllerClass;
import io.varhttp.HttpMethod;

import javax.inject.Inject;

@ControllerClass
public class ContextDependentController {
	private IMyContext myContext;

	@Inject
	public ContextDependentController(IMyContext myContext) {
		this.myContext = myContext;
	}

	@Controller(path = "/contextdependent", httpMethods = {HttpMethod.GET})
	public String contextdependent() {
		return myContext.contextResult();
	}
}
