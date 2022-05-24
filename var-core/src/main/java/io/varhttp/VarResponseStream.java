package io.varhttp;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class VarResponseStream implements ResponseStream {

	private final HttpServletResponse response;
	private final ControllerContext context;
	private final Serializer serializer;

	public VarResponseStream(ControllerContext controllerContext, Serializer serializer) {
		this.response = controllerContext.response();
		this.context = controllerContext;
		this.serializer = serializer;
	}

	@Override
	public BufferedWriter getContentWriter(String fileName, String contentType, Charset charset) {
		response.addHeader("Content-Disposition", "attachment; filename=\"" + fileName + '"');
		response.setContentType(context.acceptedTypes().limitTo(contentType).getHighestPriority().getType());
		response.setCharacterEncoding(charset.name());
		try {
			return new BufferedWriter(new OutputStreamWriter(new BufferedOutputStream(response.getOutputStream()), charset));
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public OutputStream getOutputStream(String contentType, Charset charset) {
		response.setContentType(context.acceptedTypes().limitTo(contentType).getHighestPriority().getType());
		if(charset != null) {
			response.setCharacterEncoding(charset.name());
		}
		try {
			return response.getOutputStream();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void write(Object object) {
		write(object, context.getContentType());
	}

	@Override
	public void write(Object object, String forcedContentType) {
		try (OutputStreamWriter streamWriter = new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8)) {

			String contentType;
			if (object instanceof String) {
				contentType = getContentType(forcedContentType != null ? forcedContentType : "text/plain", false);
				streamWriter.write((String) object);
			} else {
				contentType = getContentType(forcedContentType, true);
				serializer.serialize(streamWriter, object, contentType);
			}
			response.setContentType(contentType);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private String getContentType(String forcedContentType, boolean limitToSerializerSupportTypes) {
		ContentTypes validContentTypes = context.acceptedTypes().limitTo(forcedContentType);

		if(validContentTypes.isEmpty()) {
			return throwContentTypeError(context.acceptedTypes());
		}

		try {
			if(limitToSerializerSupportTypes) {
				validContentTypes = validContentTypes.limitTo(serializer.supportedTypes());
			}

			return validContentTypes.getHighestPriority().getType();
		} catch (ContentTypeException e) {
			return throwContentTypeError(validContentTypes);
		}
	}

	private String throwContentTypeError(ContentTypes contentTypes) throws ContentTypeException {
		String accepted = contentTypes
				.stream()
				.map(ContentTypes.ContentType::getType)
				.collect(Collectors.joining(","));

		throw new ContentTypeException("Requested Content-Type " + accepted + " is not supported");
	}
}