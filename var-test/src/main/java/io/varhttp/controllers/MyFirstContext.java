package io.varhttp.controllers;

public class MyFirstContext implements IMyContext {

	@Override
	public String contextResult() {
		return "MyFirstContext";
	}
}
