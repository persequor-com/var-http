package io.varhttp;

import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.List;

public interface Serializer {

	void serialize(Writer writer, Object content, String contentType);

	<T> T deserialize(Reader content, Type type, String contentType);

	List<String> supportedTypes();
}
