package io.varhttp;

import java.lang.reflect.Method;
import java.util.Set;

public class ControllerMatch {
	private Method method;
	private String path;
	private Set<HttpMethod> httpMethods;
	private String contentType;

	public ControllerMatch(Method method, String path, Set<HttpMethod> httpMethods, String contentType) {
		this.method = method;
		this.path = path;
		this.httpMethods = httpMethods;
		this.contentType = contentType;
	}

	public Method getMethod() {
		return method;
	}

	public String getPath() {
		return path;
	}

	public Set<HttpMethod> getHttpMethods() {
		return httpMethods;
	}

	public String getContentType() {
		return contentType;
	}
}
