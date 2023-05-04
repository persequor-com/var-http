package io.varhttp.controllers.withfilters;

import io.varhttp.Filter;
import io.varhttp.VarFilter;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class LoggingFilter implements VarFilter, javax.servlet.Filter {
	private FilterCatcher filterCatcher;

	@Inject
	public LoggingFilter(FilterCatcher filterCatcher) {
		this.filterCatcher = filterCatcher;
	}

	@Override
	public void init(Method method, Filter f, Annotation annotation) {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		filterCatcher.add("Logging was called before");
		try {
			chain.doFilter(request, response);
		} catch (Throwable th) {
			filterCatcher.add(th.getMessage());
		} finally {
			filterCatcher.add("Logging was called after");
		}
	}
}
