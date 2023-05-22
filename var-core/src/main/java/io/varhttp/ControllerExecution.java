package io.varhttp;

import io.varhttp.parameterhandlers.IParameterHandler;
import org.slf4j.LoggerFactory;

import javax.inject.Provider;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
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
			filters.add(filterExecution);
			Iterator<Object> iterator = filters.iterator();
			VarServletFilterChain chain = new VarServletFilterChain(context, iterator.next(), iterator);
			Enumeration<String> acceptHeaders = context.request().getHeaders("Accept");
			while (acceptHeaders.hasMoreElements()) {
				context.acceptedTypes().add(acceptHeaders.nextElement());
			}
			if (context.acceptedTypes().isEmpty()) {
				context.acceptedTypes().add("*/*");
			}

			chain.doFilter(context.request(), context.response());
		} catch (WrappedServletException e) {
			// Controller logic threw exception
			Throwable cause = e.getCause() == null ? e : e.getCause();
			fail(exceptionRegistry.getResponseCode(cause.getClass(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
					, cause
					, context.response()
			);
		} catch (ExceptionInInitializerError | RuntimeException| ServletException | IOException e) {
			fail(exceptionRegistry.getResponseCode(e.getClass(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
					, e
					, context.response()
			);
		}finally{
			context.complete();
		}
	}

	private void fail(int responseCode, Throwable e, HttpServletResponse response) {
		logger.error("Controller execution failed", e);
		response.setStatus(500);
		fail(responseCode, e.getClass().getName() + " " + e.getMessage(), response);
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
}
