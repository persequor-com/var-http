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
	private Serializer serializer;

	public VarResponseStream(HttpServletResponse response, Serializer serializer) {
		this.response = response;
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
			serializer.serialize(streamWriter, object, response.getHeaders("Content-Type").stream().findFirst().orElse("application/json"));
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