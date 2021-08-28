package io.varhttp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ControllerContext {
	private final HttpServletRequest request;
	private final HttpServletResponse response;

	public ControllerContext(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}

	public HttpServletRequest request() {
		return request;
	}

	public HttpServletResponse response() {
		return response;
	}

	public RequestParameters getParameters() {
		return new RequestParametersImplementation(request);
	}
}
