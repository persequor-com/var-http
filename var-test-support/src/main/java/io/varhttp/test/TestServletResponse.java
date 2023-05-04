package io.varhttp.test;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TestServletResponse implements HttpServletResponse {

	private int status = 200;
	private final PrintWriter printWriter;
	private final Map<String, Collection<String>> headers = new HashMap<>();
	final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	final ServletOutputStream servletOutputStream = new ServletOutputStream() {

		@Override
		public boolean isReady() {
			return true;
		}

		@Override
		public void setWriteListener(WriteListener writeListener) {
			throw new RuntimeException("?");
		}

		@Override
		public void write(int b) throws IOException {
			TestServletResponse.this.committed = true;
			outputStream.write(b);
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			TestServletResponse.this.committed = true;
			outputStream.write(b, off, len);
		}
	};
	private boolean committed = false;

	public TestServletResponse() {
		this.printWriter = new PrintWriter(servletOutputStream);
	}

	@Override
	public void addCookie(Cookie cookie) {
		this.addHeader("Set-Cookie", cookie.getName() + "=" + cookie.getValue() + (cookie.getMaxAge() > -1 ? "; Max-Age=" + cookie.getMaxAge() : "") + (cookie.getSecure() ? "; Secure" : "") + (cookie.isHttpOnly() ? "; HttpOnly" : "") + (cookie.getPath() != null ? "; Path=" + cookie.getPath() : ""));
	}

	@Override
	public boolean containsHeader(String name) {
		return this.headers.containsKey(name.toLowerCase());
	}

	@Override
	public String encodeURL(String url) {
		return URLEncoder.encode(url);
	}

	@Override
	public String encodeRedirectURL(String url) {
		return URLEncoder.encode(url);
	}

	@Override
	public String encodeUrl(String url) {
		return URLEncoder.encode(url);
	}

	@Override
	public String encodeRedirectUrl(String url) {
		return URLEncoder.encode(url);
	}

	@Override
	public void sendError(int sc, String msg) throws IOException {

	}

	@Override
	public void sendError(int sc) throws IOException {

	}

	@Override
	public void sendRedirect(String location) throws IOException {

	}

	@Override
	public void setDateHeader(String name, long date) {

	}

	@Override
	public void addDateHeader(String name, long date) {

	}

	@Override
	public void setHeader(String name, String value) {
		addHeader(name, value);
	}

	@Override
	public void addHeader(String name, String value) {
		headers.put(name.toLowerCase(), Collections.singleton(value));
	}

	@Override
	public void setIntHeader(String name, int value) {

	}

	@Override
	public void addIntHeader(String name, int value) {

	}

	@Override
	public void setStatus(int sc) {
		this.status = sc;
	}

	@Override
	public void setStatus(int sc, String sm) {
		this.status = sc;
	}

	@Override
	public int getStatus() {
		return status;
	}

	@Override
	public String getHeader(String name) {
		Collection<String> strings = headers.get(name.toLowerCase());
		if (strings != null) {
			return strings.stream().findFirst().get();
		} else {
			return null;
		}
	}

	@Override
	public Collection<String> getHeaders(String name) {
		return headers.get(name.toLowerCase());
	}

	@Override
	public Collection<String> getHeaderNames() {
		return headers.keySet();
	}

	@Override
	public String getCharacterEncoding() {
		return getHeader("Character-Encoding");
	}

	@Override
	public String getContentType() {
		return getHeader("Content-Type");
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return servletOutputStream;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		return printWriter;
	}

	@Override
	public void setCharacterEncoding(String charset) {
		this.addHeader("Character-Encoding", charset);
	}

	@Override
	public void setContentLength(int len) {

	}

	@Override
	public void setContentLengthLong(long len) {

	}

	@Override
	public void setContentType(String type) {
		this.addHeader("Content-Type", type);
	}

	@Override
	public void setBufferSize(int size) {

	}

	@Override
	public int getBufferSize() {
		return 0;
	}

	@Override
	public void flushBuffer() throws IOException {

	}

	@Override
	public void resetBuffer() {

	}

	@Override
	public boolean isCommitted() {
		return committed;
	}

	@Override
	public void reset() {

	}

	@Override
	public void setLocale(Locale loc) {

	}

	@Override
	public Locale getLocale() {
		return null;
	}
}
