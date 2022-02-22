package io.varhttp;

import java.net.URL;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class VarResponseHeader implements ResponseHeader {
	private final HttpServletResponse response;
	private final String classPath;
	private final ControllerContext context;

	public VarResponseHeader(ControllerContext controllerContext, String classPath) {
		this.response = controllerContext.response();
		this.context = controllerContext;
		this.classPath = classPath;
	}

	@Override
	public void setStatus(int httpResponseCode) {
		response.setStatus(httpResponseCode);
	}

	@Override
	public void addHeader(String name, String value) {
		if ("Content-Type".equalsIgnoreCase(name)) {
			setContentType(value);
		} else {
			response.addHeader(name, value);
		}
	}

	@Override
	public void setHeader(String name, String value) {
		if ("Content-Type".equalsIgnoreCase(name)) {
			setContentType(value);
		} else {
			response.setHeader(name, value);
		}
	}

	@Override
	public void redirect(String path) {
		response.setHeader("Location", path);
	}

	@Override
	public void redirectRelative(String path) {
		response.setHeader("Location", classPath+"/"+path);
	}

	@Override
	public void redirect(URL url) {
		response.setHeader("Location", url.toString());
	}

	@Override
	public void addCookie(Cookie cookie) {
		this.response.addCookie(cookie);
	}

	@Override
	public void setContentType(String s) {
		context.setContentType(s);
	}
}