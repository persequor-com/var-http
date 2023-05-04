package io.varhttp;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import java.util.Collections;
import java.util.Enumeration;

public class JdkServletConfig implements ServletConfig {

	private final HttpServlet httpServlet;
	private final JdkServletContext servletContext = new JdkServletContext(null);

	public JdkServletConfig(HttpServlet httpServlet) {
		this.httpServlet = httpServlet;
	}

	@Override
	public String getServletName() {
		return httpServlet.getServletName();
	}

	@Override
	public ServletContext getServletContext() {
		return servletContext;
	}

	@Override
	public String getInitParameter(String name) {
		return null; // by the contract
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		return Collections.emptyEnumeration(); // by the contract
	}
}
