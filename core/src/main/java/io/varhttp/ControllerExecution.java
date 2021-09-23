package io.varhttp;

import io.varhttp.parameterhandlers.IParameterHandler;
import org.slf4j.LoggerFactory;

import javax.inject.Provider;
import javax.servlet.Filter;
import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ControllerExecution  {
	private final VarFilterExecution filterExecution;
	org.slf4j.Logger logger = LoggerFactory.getLogger(ControllerExecution.class);

	private final Method method;
	private final ExceptionRegistry exceptionRegistry;
	private ControllerMatch matchResult;
	private final List<Object> filters;

	public ControllerExecution(Provider<Object> controllerImplementation
			, Method method
			, IParameterHandler[] args
			, ParameterHandler parameterHandler
			, ExceptionRegistry exceptionRegistry
			, ControllerMatch matchResult
			, List<Object> filters
	) {
		this.method = method;
		this.exceptionRegistry = exceptionRegistry;
		this.matchResult = matchResult;
		this.filters = filters;
		this.filterExecution = new VarFilterExecution(controllerImplementation, method, args, parameterHandler, matchResult);
	}

	public void execute(ControllerContext context) {
		try {
			List<Object> filters = new ArrayList<>(this.filters);
			filters.add((Filter)(request, response, chain) -> {
				filterExecution.doFilter(context);
			});
			Iterator<Object> iterator = filters.iterator();
			VarFilterChain chain = new VarFilterChain(context, iterator.next(), iterator);
			chain.doFilter(context.request(), context.response());
		} catch (ServletException e) {
			// Controller logic threw exception
			Throwable cause = e.getCause() == null ? e : e.getCause();
			fail(exceptionRegistry.getResponseCode(cause.getClass(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
					, cause
					, context.response()
			);
		} catch (ExceptionInInitializerError | RuntimeException | IOException e) {
			fail(exceptionRegistry.getResponseCode(e.getClass(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
					, e
					, context.response()
			);
		}
	}

	private void fail(int responseCode, Throwable e, HttpServletResponse response) {
		logger.error("Controller execution failed", e);
		response.setStatus(500);
		fail(responseCode, e.getClass().getName() + " \n" + e.getMessage(), response);
	}

	public void fail(int responseCode, String message, HttpServletResponse response) {
		try (Writer writer = new OutputStreamWriter(response.getOutputStream())) {
			response.setStatus(responseCode);
			writer.append(message);
		} catch (IOException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	public Method getMethod() {
		return method;
	}

	private static class VarFilterChain implements FilterChain {
		private ControllerContext context;
		private final Object current;
		private FilterChain chain = null;

		public VarFilterChain(ControllerContext context, Object current, Iterator<Object> iterator) {
			this.context = context;
			this.current = current;
			if (iterator.hasNext()) {
				chain = new VarFilterChain(context, iterator.next(), iterator);
			}
		}

		@Override
		public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
			if (current instanceof Filter) {
				((Filter) current).doFilter(request, response, chain);
			} else if(current instanceof VarFilterExecution) {
				((VarFilterExecution) current).doFilter(context);
				chain.doFilter(request, response);
			} else {
				throw new VarInitializationException("Invalid filter type: "+current.getClass().getName());
			}
		}
	}

}
