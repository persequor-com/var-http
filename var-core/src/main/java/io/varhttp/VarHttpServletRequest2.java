package io.varhttp;

import com.sun.net.httpserver.HttpsServer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class VarHttpServletRequest2 extends HttpServletRequestWrapper {

	private final VarConfig config;

	public VarHttpServletRequest2(VarConfig config, HttpServletRequest request) {
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
}
