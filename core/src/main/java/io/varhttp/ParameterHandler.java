package io.varhttp;

import io.varhttp.parameterhandlers.IParameterHandler;
import io.varhttp.parameterhandlers.IParameterHandlerMatcher;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ParameterHandler {

	private final Serializer serializer;
	private final ParameterHandlerMatcherFactory handlerMatcherFactory;
	private final SortedSet<IParameterHandlerMatcher> parameterHandlers = new TreeSet<>(new Comparator<IParameterHandlerMatcher>() {
		@Override
		public int compare(IParameterHandlerMatcher paramHandler1, IParameterHandlerMatcher paramHandler2) {
			int i = paramHandler1.compareTo(paramHandler2);
			return i != 0 ? i : 1; //not return 0 to always insert and not miss any param handler
		}
	});

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

}
