package io.varhttp.parameterhandlers;

import io.varhttp.ControllerContext;
import io.varhttp.Serializer;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

public class RequestBodyHandler implements IParameterHandler {
	private MatchContext matchContext;
	private Serializer serializer;

	public RequestBodyHandler(MatchContext matchContext, Serializer serializer) {
		this.matchContext = matchContext;
		this.serializer = serializer;
	}

	@Override
	public Object handle(ControllerContext controllerContext) {
		try {
			Reader body = controllerContext.request().getReader();
			if (String.class.isAssignableFrom(matchContext.getType())) {
				return toString(body);
			}

			Type type = matchContext.getParameter().getParameterizedType();
			if (Optional.class.isAssignableFrom(matchContext.getType())) {
				type = ((ParameterizedType)type).getActualTypeArguments()[0];
			}
			Object bodyString;
			if (type.getTypeName().equals(String.class.getName())) {
				bodyString = toString(body);
			} else {
				bodyString = serializer.deserialize(body, type, controllerContext.request().getContentType());
			}
			if (Optional.class.isAssignableFrom(matchContext.getType())) {
				return Optional.ofNullable(bodyString);
			}
			return bodyString;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private String toString(Reader reader) {
		try {
			char[] arr = new char[1024];
			StringBuilder buffer = new StringBuilder();
			int numCharsRead;
			while ((numCharsRead = reader.read(arr, 0, arr.length)) != -1) {
				buffer.append(arr, 0, numCharsRead);
			}
			if(buffer.length() == 0) {
				return null;
			}
			return buffer.toString();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		} finally {
			try {
				reader.close();
			} catch (IOException exception) {
				throw new RuntimeException(exception);
			}
		}
	}
}
