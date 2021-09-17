package io.varhttp;

import io.varhttp.parameterhandlers.IParameterHandler;
import io.varhttp.parameterhandlers.IParameterHandlerMatcher;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.SortedSet;
import java.util.TreeSet;

@Singleton
public class ParameterHandler {

	private final Serializer serializer;
	private final ParameterHandlerMatcherFactory handlerMatcherFactory;
	private final SortedSet<IParameterHandlerMatcher> parameterHandlers = new TreeSet<>();

	@Inject
	public ParameterHandler(Serializer serializer, ParameterHandlerMatcherFactory handlerMatcherFactory) {
		this.serializer = serializer;
		this.handlerMatcherFactory = handlerMatcherFactory;
	}

	public void addParameterHandler(Class<? extends IParameterHandlerMatcher> handlerMatcher) {
		parameterHandlers.add(handlerMatcherFactory.get(handlerMatcher));
	}

	public IParameterHandler[] initializeHandlers(Method method, String baseUri, String classPath){
		IParameterHandler[] args = new IParameterHandler[method.getParameterCount()];
		String path = baseUri; // + controller.path();
		for (int i = 0; i < method.getParameterCount(); i++) {
			Parameter parameter = method.getParameters()[i];
			for (IParameterHandlerMatcher handlerMatcher : parameterHandlers) {
				IParameterHandler handler = handlerMatcher.getHandlerIfMatches(method, parameter, path, classPath);
				if (handler != null) {
					args[i] = handler;
					continue;
				}
			}
		}

		return args;
	}

	public void handleReturnResponse(Object response, ControllerContext context, ContentTypes types) {
		if (response != null && !(response instanceof Void)) {
			new VarResponseStream(context.response(), serializer).setTypes(types).write(response);
		}
	}







//	public Function<ControllerContext, Object>[] addPathVariables(Function<ControllerContext, Object>[] handlers, HttpServletRequest request) {
//		Function<ControllerContext, Object>[] args = handlers.clone();
//		String fullPath = request.getServletPath()+request.getPathInfo();
//		Matcher m = pattern.matcher(fullPath);
//		if (m.matches()) {
//			for (int i = 0; i < pathVariables.size(); i++) {
//				Class<?> type = pathVariables.get(i).getType();
//				String value = m.group(i + 1);
//				args[pathVariables.get(i).getArgno()] = (r -> convert(value, type, null));
//			}
//		}
//		return args;
//	}
}
