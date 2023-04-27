package io.varhttp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ControllerContext {
	private final HttpServletRequest request;
	private final HttpServletResponse response;
	private final ContentTypes accepted = new ContentTypes();
	private VarFilterChain filterChain;

	public ControllerContext(HttpServletRequest request, HttpServletResponse response, VarConfig varConfig) {
		this.request = new VarHttpServletRequest(varConfig, request);
		this.response = response;
	}

	public HttpServletRequest request() {
		return request;
	}

	public HttpServletResponse response() {
		return response;
	}

	public ContentTypes acceptedTypes() {
		return accepted;
	}

	public RequestParameters getParameters() {
		return new RequestParametersImplementation(request);
	}

	public VarFilterChain getFilterChain() {
		return filterChain;
	}

	public void setFilterChain(VarFilterChain filterChain) {
		this.filterChain = filterChain;
	}

	public void setContentType(String contentType) {
		response.setContentType(contentType);
	}

	public String getContentType() {
		return response().getContentType();
	}
}
