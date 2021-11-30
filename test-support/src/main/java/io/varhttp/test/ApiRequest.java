/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2021-11-22
 */
package io.varhttp.test;

import io.varhttp.Serializer;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;

public class ApiRequest {
	final ApiParameters parameters;
	final HttpHeaders headers = new HttpHeaders();
	String content = "";

	private final ThrowingFunction<ApiRequest, HttpResponse, IOException> execution;
	private final HttpHeaders defaultHeaders;
	private final Serializer serializer;

	public ApiRequest(HttpHeaders defaultHeaders,
					  Serializer serializer, ThrowingFunction<ApiRequest, HttpResponse, IOException> execution) {
		this.serializer = serializer;
		this.execution = execution;
		this.defaultHeaders = defaultHeaders;
		parameters = new ApiParameters();
	}

	public ApiRequest parameter(String key, String... value) {
		parameters.add(key, value);
		return this;
	}

	public ApiRequest header(String key, String... value) {
		headers.add(key, Arrays.asList(value));
		return this;
	}

	public ApiResult execute() {
		try {
			defaultHeaders.forEach(headers::putIfNotPresent);
			return new ApiResult(execution.apply(this), serializer);
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	public ApiRequest accept(String accept) {
		headers.add("Accept", accept);
		return this;
	}

	public ApiRequest basicAuth(String username, String password){
		String encoded = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
		headers.put("authorization", "Basic " + encoded);
		return this;
	}

	public ApiRequest acceptEncoding(String s) {
		headers.add("Accept-Encoding", s);
		return this;
	}

	public ApiRequest transferEncoding(String encoding) {
		headers.add("Transfer-Encoding", encoding);
		return this;
	}

	public ApiRequest contentType(String contentType) {
		headers.add("Content-Type", contentType);
		return this;
	}

	public ApiRequest cookies(Collection<String> cookies) {
		headers.add("cookie", cookies);
		return this;
	}

	public ApiRequest content(String content, String contentType) {
		contentType(contentType);
		this.content = content;
		return this;
	}

	public ApiRequest contentSerialized(Object content, String contentType) {
		contentType(contentType);
		StringWriter writer = new StringWriter();
		serializer.serialize(writer, content, contentType);
		this.content = writer.toString();
		return this;
	}
}
