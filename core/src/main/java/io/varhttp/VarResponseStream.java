package io.varhttp;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

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
		response.setContentType(contentType);
		response.setCharacterEncoding(charset.name());
		try {
			return new BufferedWriter(new OutputStreamWriter(new BufferedOutputStream(response.getOutputStream()), charset));
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public OutputStream getOutputStream(String contentType, Charset charset) {
		response.setContentType(contentType);
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
		try (OutputStreamWriter streamWriter = new OutputStreamWriter(response.getOutputStream(), "UTF-8")) {

			if (object instanceof String) {
				String contentType = context.getContentType() != null ? context.getContentType() : "text/plain";
				ContentTypes validContentTypes = context.acceptedTypes().limitTo(contentType);
				streamWriter.write((String) object);
				response.setContentType(validContentTypes.getHighestPriority().getType());
			} else {
				ContentTypes validContentTypes = context.acceptedTypes().limitTo(context.getContentType());
				String contentType = validContentTypes.limitTo(serializer.supportedTypes()).getHighestPriority().getType();
				serializer.serialize(streamWriter, object, contentType);
				response.setContentType(contentType);
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void write(Object content, String contentType) {
		try (OutputStreamWriter streamWriter = new OutputStreamWriter(response.getOutputStream(), "UTF-8")) {
			if (content instanceof String) {
				streamWriter.write((String) content);
			} else {
				serializer.serialize(streamWriter, content, contentType);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}