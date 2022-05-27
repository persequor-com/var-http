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
		response.setContentType(context.acceptedTypes().limitTo(contentType).getHighestPriority(context.acceptedTypes()).getType());
		response.setCharacterEncoding(charset.name());
		try {
			return new BufferedWriter(new OutputStreamWriter(new BufferedOutputStream(response.getOutputStream()), charset));
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public OutputStream getOutputStream(String contentType, Charset charset) {
		response.setContentType(context.acceptedTypes().limitTo(contentType).getHighestPriority(context.acceptedTypes()).getType());
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

			if (object instanceof String) {
				String contentType = forcedContentType != null ? forcedContentType : "text/plain";
				ContentTypes validContentTypes = context.acceptedTypes().limitTo(contentType);
				response.setContentType(validContentTypes.getHighestPriority(context.acceptedTypes()).getType());
				streamWriter.write((String) object);
			} else {
				ContentTypes validContentTypes = context.acceptedTypes().limitTo(forcedContentType);
				String contentType = validContentTypes.limitTo(serializer.supportedTypes()).getHighestPriority(context.acceptedTypes()).getType();
				response.setContentType(contentType);
				serializer.serialize(streamWriter, object, contentType);
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}