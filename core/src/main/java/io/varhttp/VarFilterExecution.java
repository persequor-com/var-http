package io.varhttp;

import io.varhttp.parameterhandlers.IParameterHandler;
import io.varhttp.parameterhandlers.VarFilterChainParameterHandler;

import javax.inject.Provider;
import javax.servlet.ServletException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Stream;

public class VarFilterExecution {
	private final Provider<Object> controllerImplementation;

	public final Method method;
	private final IParameterHandler[] args;
	private final ParameterHandler parameterHandler;
	public final boolean isController;
	public final boolean containsChain;
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
		this.isController = method.getAnnotation(Controller.class) != null;
		this.containsChain = Arrays.stream(method.getParameterTypes()).anyMatch(c -> VarFilterChain.class == c);
	}

	public void doFilter(ControllerContext context) throws IOException, ServletException {
		try {
			Object[] methodArgs = Stream.of(args).map(f -> f == null ? null : f.handle(context)).toArray();

			Object responseObject = method.invoke(controllerImplementation.get(), methodArgs);
			if (!containsChain && context.getFilterChain() != null) {
				context.getFilterChain().proceed();
			}
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

		} catch (InvocationTargetException e) {
			throw new WrappedServletException(e.getCause());
		} catch (Exception e) {
			throw new WrappedServletException(e);
		}
	}
}
