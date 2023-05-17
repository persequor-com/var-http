package io.varhttp;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.Principal;

public class VarHttpServletRequest extends HttpServletRequestWrapper {

	private final VarConfig config;

	public VarHttpServletRequest(VarConfig config, HttpServletRequest request) {
		super(request);
		this.config = config;
	}

	@Override
	public boolean isSecure() {
		if(config.isForceRequestSecure()) {
			return config.isForceRequestSecure();
		}

		if(getHeader("X-Forwarded-Proto") != null) {
			return getHeader("X-Forwarded-Proto").equalsIgnoreCase("https");
		}

		return super.isSecure();
	}

	@Override
	public String getRemoteUser() {
		throw new UnsupportedOperationException("Not yet implemented in var-http");
	}

	@Override
	public boolean isUserInRole(String role) {
		return false;
	}

	@Override
	public Principal getUserPrincipal() {
		return () -> "Anonymous";
	}

	@Override
	public String getRequestedSessionId() {
		throw new UnsupportedOperationException("Not yet implemented in var-http");
	}


	@Override
	public HttpSession getSession(boolean create) {
		throw new UnsupportedOperationException("Not yet implemented in var-http");
	}

	@Override
	public HttpSession getSession() {
		throw new UnsupportedOperationException("Not yet implemented in var-http");
	}

	@Override
	public String changeSessionId() {
		throw new UnsupportedOperationException("Not yet implemented in var-http");
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		throw new UnsupportedOperationException("Not yet implemented in var-http");
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		throw new UnsupportedOperationException("Not yet implemented in var-http");
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		throw new UnsupportedOperationException("Not yet implemented in var-http");
	}

	@Override
	public boolean isRequestedSessionIdFromUrl() {
		throw new UnsupportedOperationException("Not yet implemented in var-http");
	}

	@Override
	public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
		throw new UnsupportedOperationException("Not yet implemented in var-http");
	}

	@Override
	public void login(String username, String password) throws ServletException {
		throw new UnsupportedOperationException("Not yet implemented in var-http");
	}

	@Override
	public void logout() throws ServletException {
		throw new UnsupportedOperationException("Not yet implemented in var-http");
	}
}
