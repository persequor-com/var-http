package io.varhttp.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class HttpResponse {

	private HttpHeaders headers;
	private int statusCode;
	private InputStream contentStream;

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
		if (content == null) {
			StringBuilder textBuilder = new StringBuilder();
			try (Reader reader = new BufferedReader(new InputStreamReader(contentStream, StandardCharsets.UTF_8))) {
				int c = 0;
				while ((c = reader.read()) != -1) {
					textBuilder.append((char) c);
				}
				return content = textBuilder.toString();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return content;
	}

	public InputStream downloadContent() {
		return contentStream;
	}


	public HttpResponse setContent(InputStream content) {
		this.contentStream = content;
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
