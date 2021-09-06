package io.varhttp;

import io.varhttp.parameterhandlers.IParameterHandler;
import io.varhttp.parameterhandlers.IParameterHandlerMatcher;

import javax.enterprise.util.TypeLiteral;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Singleton
public class ParameterHandler {
	private final List<PathVariableInfo> pathVariables = new ArrayList<>();
	private Pattern pattern;
	private Pattern pathVariablePattern = Pattern.compile("\\{(.*)\\}");
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

	public Set<HttpMethod> initializeHttpMethods(Method method) {
		Controller controller = method.getAnnotation(Controller.class);
		HttpMethod[] allowedMethods = controller.httpMethods();
		if (allowedMethods.length == 0) {
			allowedMethods = HttpMethod.values();
		}
		return new HashSet<>(Arrays.asList(allowedMethods));
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

	public void handleReturnResponse(Object response, ControllerContext context) {
		if (response != null && !(response instanceof Void)) {
			String contentType = "text/html";
			if (context.response().getContentType() != null) {
				contentType = context.response().getContentType();
			} else if (!(response instanceof String)) {
				contentType = "application/json";
			}
			new VarResponseStream(context.response(), serializer).write(response, contentType);
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
