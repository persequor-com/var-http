package io.varhttp;

import io.varhttp.parameterhandlers.IParameterHandler;
import io.varhttp.parameterhandlers.VarFilterChainParameterHandler;

import javax.inject.Provider;
import javax.servlet.ServletException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class VarFilterExecution {
	private final Provider<Object> controllerImplementation;

	public final Method method;
	private final IParameterHandler[] args;
	private final ParameterHandler parameterHandler;
	public final boolean isController;
	public final boolean containsChain;
	private ControllerMatch matchResult;
	private Consumer<VarFilter> filterConsumer;

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

			Object controllerInstance = controllerImplementation.get();
			if (filterConsumer != null && controllerInstance instanceof VarFilter) {
				filterConsumer.accept((VarFilter) controllerInstance);
			}
			Object responseObject = method.invoke(controllerInstance, methodArgs);
			if (!containsChain && context.getFilterChain() != null) {
				context.getFilterChain().proceed();
			}

			if (!"".equals(matchResult.getContentType())) {
				context.setContentType(matchResult.getContentType());
			}

			parameterHandler.handleReturnResponse(responseObject, context);

		} catch (Exception e) {
			unWrapOrThrow(e);
		}
	}

	private void unWrapOrThrow(Throwable t) throws ServletException, IOException {
		if (t instanceof ServletException) {
			throw (ServletException) t;
		} else if (t instanceof IOException) {
			throw (IOException) t;
		} else if (t instanceof RuntimeException) {
			throw (RuntimeException) t;
		} else if (t instanceof InvocationTargetException) {
			unWrapOrThrow(t.getCause());
		} else {
			throw new WrappedServletException(t);
		}
	}

	public VarFilterExecution ifVarFilter(Consumer<VarFilter> filterConsumer) {
		this.filterConsumer = filterConsumer;
		return this;
	}
}
