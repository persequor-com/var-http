/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2021-11-22
 */
package io.varhttp.test;

import io.varhttp.Serializer;

import java.io.StringReader;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ApiResult {

	private final HttpResponse response;
	private final Serializer serializer;

	public ApiResult(HttpResponse response, Serializer serializer) {
		this.response = response;
		this.serializer = serializer;
	}

	public ApiResult hasStatusCode(int code) {
		assertEquals("Expected status code " + code + " got " + response.getStatusCode() + "\n" + response.getContent(), code, response.getStatusCode());
		return this;
	}

	public ApiResult isOk() {
		return hasStatusCode(200);
	}

	public ApiResult isInternalError() {
		return hasStatusCode(500);
	}

	public <T> T getContent(Class<T> clazz) {
		return serializer.deserialize(new StringReader(getContent()), clazz, response.getHeaders().get("content-type"));
	}

	public ApiResult badRequest() {
		return hasStatusCode(400);
	}

	public ApiResult isUnsupportedMediaType() {
		return hasStatusCode(415);
	}

	public ApiResult isCreated() {
		return hasStatusCode(201);
	}

	public ApiResult notFound() {
		return hasStatusCode(404);
	}

	public ApiResult notAuthorized() {
		return hasStatusCode(401);
	}

	public ApiResult isForbidden() {
		return hasStatusCode(403);
	}

	public String getContent() {
		return response.getContent();
	}

	public ApiResult contentType(String contentType) {
		assertEquals(contentType, response.getHeaders().get("content-type"));
		return this;
	}

	public ApiResult content(String expected) {
		assertEquals(expected, getContent());
		return this;
	}

	public ApiResult header(String name, String expectedValue) {
		assertEquals(expectedValue, response.getHeaders().get(name));
		return this;
	}

	public ApiResult headerSize(String name, int expectedSize) {
		List<String> headerValues = response.getHeaders().getAll(name);
		assertEquals(expectedSize, headerValues == null ? 0 : headerValues.size());
		return this;
	}

	public String getHeader(String name) {
		return response.getHeaders().get(name);
	}

	public String getLocation() {
		return getHeader("location");
	}

	public List<String> getCookies() {
		List<String> cookieHeaders = response.getHeaders().getAll("set-cookie");
		return cookieHeaders == null ? Collections.emptyList() : cookieHeaders;
	}
}
