package io.varhttp;

public class ControllerFilter {
	public boolean accepts(Request request, ControllerExecution execution) {
		return true;
	}
}
