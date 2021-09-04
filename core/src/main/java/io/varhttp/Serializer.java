package io.varhttp;

import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;

public interface Serializer {
	String serialize(Object content, String contentType);

	void serialize(Writer writer, Object content, String contentType);

	<T> T deserialize(String content, Class<T> clazz, String contentType);

	<T> T deserialize(Reader content, Class<T> clazz, String contentType);

	<T> T deserialize(Reader content, Type type, String contentType);
}
