package io.varhttp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import javax.inject.Inject;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.List;

public class VarTestSerializer implements Serializer {
	public static final String TEXT_PLAIN = "text/plain";
	private ObjectMapper serializer;
	private XmlMapper xmlMapper;

	@Inject
	public VarTestSerializer(ObjectMapper serializer, XmlMapper xmlMapper) {
		this.serializer = serializer;
		this.xmlMapper = xmlMapper;
	}

	public String serialize(Object content, String contentType) {
		try {
			if ("application/json".equals(contentType)) {
				return serializer.writeValueAsString(content);
			} else if ("application/xml".equals(contentType)) {
				return xmlMapper.writeValueAsString(content);
			} else {
				return content.toString();
			}
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public void serialize(Writer writer, Object content, String contentType) {
		try {
			if (contentType.contains("application/json")) {
				serializer.writeValue(writer, content);
			} else if (contentType.contains("application/xml")) {
				xmlMapper.writeValue(writer, content);
			} else {
				writer.write(content.toString());
			}
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	public <T> T deserialize(String content, Class<T> clazz, String contentType) {
		try {
			return serializer.readValue(content, clazz);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public <T> T deserialize(Reader content, Class<T> clazz, String contentType) {
		try {
			return serializer.readValue(content, clazz);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public <T> T deserialize(Reader content, Type type, String contentType) {
		try {
			return (T) serializer.readValue(content, new TypeReference<Object>() {
				@Override
				public Type getType() {
					return type;
				}
			});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<String> supportedTypes() {
		return Arrays.asList("application/json", "application/xml");
	}
}