package io.varhttp.controllers.withfilters;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

public class AuthenticationFilter implements Filter {
	private FilterCatcher filterCatcher;

	@Inject
	public AuthenticationFilter(FilterCatcher filterCatcher) {
		this.filterCatcher = filterCatcher;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		filterCatcher.add("Authentication filter");
		chain.doFilter(request, response);
	}
}
