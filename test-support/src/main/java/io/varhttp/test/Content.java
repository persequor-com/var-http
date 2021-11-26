/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 
 */
package io.varhttp.test;

public class Content {
	private byte[] bytes;
	private HttpHeaders httpHeaders = new HttpHeaders();

	public static Content empty() {
		Content content = new Content();
		content.bytes = new byte[0];
		return content;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public static Content json(String json) {
		Content content = new Content();
		content.bytes = json.getBytes();
		content.httpHeaders.add("Content-Type", "application/json");
		return content;
	}

	public static Content xml(String xml) {
		Content content = new Content();
		content.bytes = xml.getBytes();
		content.httpHeaders.add("Content-Type", "application/xml");
		return content;
	}

	public static Content text(String text) {
		Content content = new Content();
		content.bytes = text.getBytes();
		content.httpHeaders.add("Content-Type", "text/plain");
		return content;
	}

	public HttpHeaders getHeaders() {
		return httpHeaders;
	}
}
