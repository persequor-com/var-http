package io.varhttp;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VarRequestHeader implements RequestHeader {
	private HttpServletRequest request;

	public VarRequestHeader(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public String getHeader(String name) {
		return String.join(", ", getHeaders(name));
	}

	@Override
	public List<String> getHeaders(String name) {
		return Collections.list(request.getHeaders(name));
	}

	@Override
	public Set<String> getHeaderNames() {
		return new HashSet<>(Collections.list(request.getHeaderNames()));
	}

	@Override
	public String getPath() {
		return request.getServletPath();
	}

	@Override
	public String getMethod() {
		return request.getMethod();
	}
}