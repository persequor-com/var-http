package io.varhttp;

public class ControllerAlreadyExistsException extends RuntimeException {
	private Request request;

	public ControllerAlreadyExistsException(Request request) {
		super("Controller already exists for path: "+request.path + " for method "+request.method.name());
		this.request = request;
	}

	public Request getRequest() {
		return request;
	}
}
