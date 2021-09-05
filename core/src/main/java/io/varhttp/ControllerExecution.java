package io.varhttp;

import io.varhttp.parameterhandlers.IParameterHandler;
import org.slf4j.LoggerFactory;

import javax.inject.Provider;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class ControllerExecution {
	org.slf4j.Logger logger = LoggerFactory.getLogger(ControllerExecution.class);

	private final Provider<Object> controllerImplementation;

	private final Method method;
	private final IParameterHandler[] args;
	private final ParameterHandler parameterHandler;
	private final ExceptionRegistry exceptionRegistry;
	private Controller controller;
	private final List<Filter> filters;
	private String classPath;

	public ControllerExecution(Provider<Object> controllerImplementation
			, Method method
			, IParameterHandler[] args
			, ParameterHandler parameterHandler
			, ExceptionRegistry exceptionRegistry
			, Controller controller
			, List<Filter> filters
			, String classPath
	) {
		this.controllerImplementation = controllerImplementation;
		this.method = method;
		this.args = args;
		this.parameterHandler = parameterHandler;
		this.exceptionRegistry = exceptionRegistry;
		this.controller = controller;
		this.filters = filters;
		this.classPath = classPath;
	}

	public void execute(ControllerContext context) {
		Object[] methodArgs = Stream.of(args).map(f -> f == null ? null : f.handle(context)).toArray();
		try {
			List<Filter> filters = new ArrayList<>(this.filters);
			filters.add((request, response, chain) -> {
				try {
					Object responseObject = method.invoke(controllerImplementation.get(), methodArgs);
					if (!"".equals(controller.contentType()) && context.response().getHeader("Content-Type") == null) {
						context.response().setHeader("Content-Type", controller.contentType());
					}
					parameterHandler.handleReturnResponse(responseObject, context);

				} catch(IllegalAccessException | InvocationTargetException e) {
					throw new ServletException(e);
				}
			});
			Iterator<Filter> iterator = filters.iterator();
			VarFilterChain chain = new VarFilterChain(iterator.next(), iterator);
			chain.doFilter(context.request(), context.response());
		} catch (ServletException e) {
			// Controller logic threw exception
			Throwable cause = e.getCause() == null ? e : e.getCause();
			fail(exceptionRegistry.getResponseCode(cause.getClass(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
					, cause
					, context.response()
			);
		} catch (ExceptionInInitializerError | RuntimeException | IOException e) {
			fail(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e, context.response());
		}

	}

	private void fail(int responseCode, Throwable e, HttpServletResponse response) {
		logger.error("Controller execution failed", e);
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
		private final Filter current;
		private FilterChain chain = null;

		public VarFilterChain(Filter current, Iterator<Filter> iterator) {
			this.current = current;
			if (iterator.hasNext()) {
				chain = new VarFilterChain(iterator.next(), iterator);
			}
		}

		@Override
		public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
			current.doFilter(request, response, chain);
		}
	}
}
