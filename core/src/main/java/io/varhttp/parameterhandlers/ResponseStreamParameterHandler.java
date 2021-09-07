package io.varhttp.parameterhandlers;

import io.varhttp.ResponseStream;
import io.varhttp.Serializer;
import io.varhttp.VarResponseStream;

import javax.inject.Inject;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class ResponseStreamParameterHandler implements IParameterHandlerMatcher {
	private Serializer serializer;

	@Inject
	public ResponseStreamParameterHandler(Serializer serializer) {
		this.serializer = serializer;
	}

	@Override
	public int getPriority() {
		return 100;
	}

	@Override
	public IParameterHandler getHandlerIfMatches(Method method, Parameter parameter, String path, String classPath) {
		if (ResponseStream.class == parameter.getType()) {
			return context ->  new VarResponseStream(context.response(), serializer);
		}
		return null;
	}


}
