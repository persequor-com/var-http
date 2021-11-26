/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2021-11-22
 */
package io.varhttp.test;

import io.varhttp.Serializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ApiRequest {
	final ApiParameters parameters;
	final HttpHeaders headers = new HttpHeaders();
	final Map<String, Object> attributes = new HashMap<>();
	private final ThrowingFunction<ApiRequest, HttpResponse, IOException> execution;
	private final HttpHeaders defaultHeaders;

	Content content = Content.empty();
	private final Serializer serializer;

	public ApiRequest(HttpHeaders defaultHeaders,
					  Serializer serializer, ThrowingFunction<ApiRequest, HttpResponse, IOException> execution) {
		this.serializer = serializer;
		this.execution = execution;
		this.defaultHeaders = defaultHeaders;
		parameters = new ApiParameters();
	}

	public ApiRequest content(Content content) {
		headers.add(content.getHeaders());
		this.content = content;
		return this;
	}

	public ApiRequest parameter(String key, String... value) {
		parameters.add(key, value);
		return this;
	}

	public ApiRequest attribute(String name, Object value) {
		attributes.put(name, value);
		return this;
	}

	public ApiResult execute() {
		try {
			defaultHeaders.forEach((name, value) -> {
				headers.putIfNotPresent(name, value);
			});
			return new ApiResult(execution.apply(this), serializer);
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	public ApiRequest accept(String accept) {
		headers.add("Accept", accept);
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
}
