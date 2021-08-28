package io.varhttp;

import javax.servlet.http.HttpServletRequest;

public class RequestParametersImplementation implements RequestParameters {
	private HttpServletRequest request;

	public RequestParametersImplementation(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public String get(String name) {

		return request.getParameter(name);
	}

	@Override
	public void remove(String name) {
		request.getParameterMap().remove(name);
	}
}
