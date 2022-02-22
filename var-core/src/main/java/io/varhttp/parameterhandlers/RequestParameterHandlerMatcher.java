package io.varhttp.parameterhandlers;

import io.varhttp.RequestParameter;

import javax.inject.Inject;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class RequestParameterHandlerMatcher implements IParameterHandlerMatcher {
	private Convert convert;

	@Inject
	public RequestParameterHandlerMatcher(Convert convert) {
		this.convert = convert;
	}

	@Override
	public int getPriority() {
		return 50;
	}

	@Override
	public IParameterHandler getHandlerIfMatches(Method method, Parameter parameter, String path, String classPath) {
		RequestParameter parameterAnnotation = parameter.getAnnotation(RequestParameter.class);
		if (parameterAnnotation == null) {
			return null;
		}
		String name = parameterAnnotation.name();
		if("".equals(name)) {
			throw new RuntimeException("Could not determine @RequestParameter name annotation for controller: " + method.getName());
		}
		Class<?> type = parameter.getType();
		RequestParameterHandler.Configuration configuration = new RequestParameterHandler.Configuration(method, parameter, type, parameterAnnotation.name(), parameterAnnotation.required(), parameterAnnotation.defaultValue());

		if (List.class.isAssignableFrom(type)) {
			ParameterizedType pType = (ParameterizedType)parameter.getParameterizedType();
			Type listElementType = pType.getActualTypeArguments()[0];
			return new RequestParameterListHandler(configuration, listElementType, convert);
		} else {
			return new RequestParameterHandler(configuration, convert);
		}
	}
}
