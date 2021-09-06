package io.varhttp.controllers;

public class MySecondContext implements IMyContext {

	@Override
	public String contextResult() {
		return "MySecondContext";
	}
}
