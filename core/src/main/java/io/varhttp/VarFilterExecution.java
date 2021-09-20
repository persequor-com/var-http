package io.varhttp;

import io.varhttp.parameterhandlers.IParameterHandler;

import javax.inject.Provider;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Stream;

public class VarFilterExecution {
	private final Provider<Object> controllerImplementation;

	private final Method method;
	private final IParameterHandler[] args;
	private final ParameterHandler parameterHandler;
	private ControllerMatch matchResult;

	public VarFilterExecution(Provider<Object> controllerImplementation
			, Method method
			, IParameterHandler[] args
			, ParameterHandler parameterHandler
			, ControllerMatch matchResult
	) {
		this.controllerImplementation = controllerImplementation;
		this.method = method;
		this.args = args;
		this.parameterHandler = parameterHandler;
		this.matchResult = matchResult;
	}

	public void doFilter(ControllerContext context) throws IOException, ServletException {
		try {
			Object[] methodArgs = Stream.of(args).map(f -> f == null ? null : f.handle(context)).toArray();

			Object responseObject = method.invoke(controllerImplementation.get(), methodArgs);
			ContentTypes types = new ContentTypes();

			if (context.response().getHeader("Content-Type") == null) {
				if (context.request().getHeader("Accept") != null) {
					types.add(context.request().getHeader("Accept"));
				}
				if (!"".equals(matchResult.getContentType())) {
					types.set(matchResult.getContentType());
				}
			}
			parameterHandler.handleReturnResponse(responseObject, context, types);

		} catch(IllegalAccessException | InvocationTargetException e) {
			throw new ServletException(e);
		}
	}
}
