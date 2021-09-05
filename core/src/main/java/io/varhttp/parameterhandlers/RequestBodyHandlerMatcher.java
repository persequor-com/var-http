package io.varhttp.parameterhandlers;

import io.varhttp.RequestBody;
import io.varhttp.Serializer;

import javax.inject.Inject;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class RequestBodyHandlerMatcher implements IParameterHandlerMatcher {
	private Serializer serializer;

	@Inject
	public RequestBodyHandlerMatcher(Serializer serializer) {
		this.serializer = serializer;
	}

	@Override
	public int getPriority() {
		return 60;
	}

	@Override
	public IParameterHandler getHandlerIfMatches(Method method, Parameter parameter, String path, String classPath) {
		RequestBody bodyAnnotation = parameter.getAnnotation(RequestBody.class);
		if (bodyAnnotation != null) {
			return new RequestBodyHandler(new MatchContext(method, parameter, parameter.getType()), serializer);
		}
		return null;
	}
}
