package io.varhttp;

import com.google.gson.Gson;

import javax.inject.Inject;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;

public class GsonSerializer implements Serializer {
	public static final String TEXT_PLAIN = "text/plain";
	private Gson gson;

	@Inject
	public GsonSerializer(Gson gson) {
		this.gson = gson;
	}

	public String serialize(Object content, String contentType) {
		return gson.toJson(content);
	}

	public void serialize(Writer writer, Object content, String contentType) {
		if (contentType.equals("application/json")) {
			gson.toJson(content, writer);
		} else {
			try {
				writer.write(content.toString());
			} catch (IOException exception) {
				throw new RuntimeException(exception);
			}
		}
	}

	public <T> T deserialize(String content, Class<T> clazz, String contentType) {
		return gson.fromJson(content, clazz);
	}

	public <T> T deserialize(Reader content, Class<T> clazz, String contentType) {
		return gson.fromJson(content, clazz);
	}

	public <T> T deserialize(Reader content, Type type, String contentType) {
		return gson.fromJson(content, type);
	}
}