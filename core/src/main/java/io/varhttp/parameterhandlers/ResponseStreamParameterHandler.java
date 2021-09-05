package io.varhttp.parameterhandlers;

import io.varhttp.ParameterHandler;
import io.varhttp.ResponseStream;
import io.varhttp.Serializer;
import io.varhttp.VarResponseStream;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.Charset;

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
