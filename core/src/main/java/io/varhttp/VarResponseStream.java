package io.varhttp;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Optional;

public class VarResponseStream implements ResponseStream {
	private final HttpServletResponse response;
	private Serializer serializer;
	private ContentTypes types = new ContentTypes();

	public VarResponseStream(HttpServletResponse response, Serializer serializer) {
		this.response = response;
		this.serializer = serializer;
	}

	public VarResponseStream setTypes(ContentTypes types) {
		this.types = types;
		return this;
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
			if (response.getHeader("Content-Type") != null) {
				types.add(response.getHeader("Content-Type"));
			} else {
				Optional<String> type = types.getType(serializer.supportedTypes());
				response.setContentType(type.orElse("application/json"));
			}

			if (object instanceof String) {
				streamWriter.write((String) object);
			} else {
				Optional<String> type = types.getType(serializer.supportedTypes());
				serializer.serialize(streamWriter, object, type.orElse("application/json"));
			}
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