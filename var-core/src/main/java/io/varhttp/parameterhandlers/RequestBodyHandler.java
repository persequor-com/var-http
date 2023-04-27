package io.varhttp.parameterhandlers;

import io.varhttp.ControllerContext;
import io.varhttp.Serializer;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

public class RequestBodyHandler implements IParameterHandler {
	private final MatchContext matchContext;
	private final Serializer serializer;

	public RequestBodyHandler(MatchContext matchContext, Serializer serializer) {
		this.matchContext = matchContext;
		this.serializer = serializer;
	}

	@Override
	public Object handle(ControllerContext controllerContext) {
		try {
			HttpServletRequest request = controllerContext.request();
			if (String.class.isAssignableFrom(matchContext.getType())) {
				return toString(request.getReader());
			}

			Type type = matchContext.getParameter().getParameterizedType();
			if (Optional.class.isAssignableFrom(matchContext.getType())) {
				type = ((ParameterizedType)type).getActualTypeArguments()[0];
			}
			Object bodyString;
			if (InputStream.class.isAssignableFrom(matchContext.getType())) {
				return request.getInputStream();
			} else if (type.getTypeName().equals(String.class.getName())) {
				bodyString = toString(request.getReader());
			} else {
				bodyString = serializer.deserialize(request.getReader(), type, request.getContentType());
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
