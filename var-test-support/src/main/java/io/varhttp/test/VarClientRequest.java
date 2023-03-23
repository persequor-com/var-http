package io.varhttp.test;

import io.varhttp.Serializer;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;

public class VarClientRequest {
	final HttpParameters parameters;
	final HttpHeaders headers = new HttpHeaders();
	String content = "";
	private ContentFormat contentFormat;
	private final ThrowingFunction<VarClientRequest, HttpResponse, IOException> execution;
	private final HttpHeaders defaultHeaders;
	private final Serializer serializer;

	public VarClientRequest(HttpHeaders defaultHeaders,
							Serializer serializer, ThrowingFunction<VarClientRequest, HttpResponse, IOException> execution) {
		this.serializer = serializer;
		this.execution = execution;
		this.defaultHeaders = defaultHeaders;
		this.parameters = new HttpParameters();
		this.contentFormat = ContentFormat.STRING_CONTENT;
	}

	public VarClientRequest parameter(String key, String... value) {
		parameters.add(key, value);
		return this;
	}

	public VarClientRequest header(String key, String... value) {
		headers.add(key, Arrays.asList(value));
		return this;
	}

	public ContentFormat getContentFormat() {
		return contentFormat;
	}

	public VarClientRequest setContentFormat(ContentFormat contentFormat) {
		this.contentFormat = contentFormat;
		return this;
	}

	public VarClientResponse execute() {
		try {
			defaultHeaders.forEach(headers::putIfNotPresent);
			return new VarClientResponse(execution.apply(this), serializer);
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	public VarClientRequest accept(String accept) {
		headers.add("Accept", accept);
		return this;
	}

	public VarClientRequest basicAuth(String username, String password){
		String encoded = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
		headers.put("authorization", "Basic " + encoded);
		return this;
	}

	public VarClientRequest acceptEncoding(String s) {
		headers.add("Accept-Encoding", s);
		return this;
	}

	public VarClientRequest transferEncoding(String encoding) {
		headers.add("Transfer-Encoding", encoding);
		return this;
	}

	public VarClientRequest contentType(String contentType) {
		headers.add("Content-Type", contentType);
		return this;
	}

	public VarClientRequest cookies(Collection<String> cookies) {
		headers.add("cookie", cookies);
		return this;
	}

	public VarClientRequest content(String content, String contentType) {
		contentType(contentType);
		this.content = content;
		return this;
	}

	public VarClientRequest contentSerialized(Object content, String contentType) {
		contentType(contentType);
		StringWriter writer = new StringWriter();
		serializer.serialize(writer, content, contentType);
		this.content = writer.toString();
		return this;
	}

	public enum ContentFormat {
		STREAM_CONTENT,
		STRING_CONTENT
	}
}
