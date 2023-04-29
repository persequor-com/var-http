package io.varhttp;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class VarHttpServletResponse extends HttpServletResponseWrapper {

	public VarHttpServletResponse(HttpServletResponse response) {
		super(response);
	}

	@Override
	public void setStatus(int sc) {
		throwIfComplete();
		super.setStatus(sc);
	}

	@Override
	public void addCookie(Cookie cookie) {
		throwIfComplete();
		super.addCookie(cookie);
	}

	@Override
	public void addHeader(String name, String value) {
		throwIfComplete();
		super.addHeader(name, value);
	}

	@Override
	public void setContentType(String type) {
		throwIfComplete();
		super.setContentType(type);
	}

	private void throwIfComplete() {
		if (this.isCommitted()) {
			throw new IllegalStateException("The response has been committed. " +
					"A committed response has already had its status code and headers written. " +
					"So, no changes of those response properties are allowed anymore.");
		}
	}
}
