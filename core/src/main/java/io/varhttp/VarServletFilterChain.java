package io.varhttp;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.Iterator;

public class VarServletFilterChain implements FilterChain, VarFilterChain {
	private ControllerContext context;
	private final Object current;
	private VarServletFilterChain chain = null;

	public VarServletFilterChain(ControllerContext context, Object current, Iterator<Object> iterator) {
		this.context = context;
		this.current = current;
		if (iterator.hasNext()) {
			chain = new VarServletFilterChain(context, iterator.next(), iterator);
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
		context.setFilterChain(chain);
		if (current instanceof javax.servlet.Filter) {
			((Filter) current).doFilter(request, response, chain);
		} else if(current instanceof VarFilterExecution) {
			((VarFilterExecution) current).doFilter(context);
		} else {
			throw new VarInitializationException("Invalid filter type: "+current.getClass().getName());
		}
	}

	@Override
	public void proceed() throws Exception {
		doFilter(context.request(), context.response());
	}
}