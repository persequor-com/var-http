/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2020-02-14
 */
package io.varhttp.test;

import java.nio.charset.Charset;

public class HttpResponse {

	private HttpHeaders headers;
	private int statusCode;
	private String content;
	private String contentType;
	private String contentEncoding;
	private Charset contentCharset;
	private int contentLoggingLimit;
	private String mediaType;
	private String statusMessage;

	public int getStatusCode() {
		return statusCode;
	}

	public HttpResponse setStatusCode(int statusCode) {
		this.statusCode = statusCode;
		return this;
	}

	public String getContent() {
		return content;
	}

	public HttpResponse setContent(String content) {
		this.content = content;
		return this;
	}

	public HttpHeaders getHeaders() {
		return headers;
	}

	public HttpResponse setHeaders(HttpHeaders headers) {
		this.headers = headers;
		return this;
	}

	public String getContentEncoding() {
		return contentEncoding;
	}

	public HttpResponse setContentEncoding(String contentEncoding) {
		this.contentEncoding = contentEncoding;
		return this;
	}

	public String getContentType() {
		return contentType;
	}

	public HttpResponse setContentType(String contentType) {
		this.contentType = contentType;
		return this;
	}

	public Charset getContentCharset() {
		return contentCharset;
	}

	public HttpResponse setContentCharset(Charset contentCharset) {
		this.contentCharset = contentCharset;
		return this;
	}

	public int getContentLoggingLimit() {
		return contentLoggingLimit;
	}

	public HttpResponse setContentLoggingLimit(int contentLoggingLimit) {
		this.contentLoggingLimit = contentLoggingLimit;
		return this;
	}

	public String getMediaType() {
		return mediaType;
	}

	public HttpResponse setMediaType(String mediaType) {
		this.mediaType = mediaType;
		return this;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public HttpResponse setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
		return this;
	}
}
