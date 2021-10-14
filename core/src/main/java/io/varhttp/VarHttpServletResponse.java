package io.varhttp;

import com.sun.net.httpserver.HttpExchange;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.time.Instant;
import java.util.Collection;
import java.util.Locale;

public class VarHttpServletResponse implements HttpServletResponse {
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
			outputStream.write(b);
		}
	};

	private final HttpExchange ex;
	private final PrintWriter printWriter;
	private int status = HttpServletResponse.SC_OK;

	public VarHttpServletResponse(HttpExchange ex) {
		this.ex = ex;
		printWriter = new PrintWriter(servletOutputStream);
	}

	@Override
	public void setContentType(String type) {
		ex.getResponseHeaders().set("Content-Type", type);
	}

	@Override
	public void setBufferSize(int size) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getBufferSize() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void flushBuffer() throws IOException {
		outputStream.flush();
	}

	@Override
	public void resetBuffer() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isCommitted() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void reset() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setLocale(Locale loc) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Locale getLocale() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setHeader(String name, String value) {
		ex.getResponseHeaders().set(name, value);
	}

	@Override
	public void addHeader(String name, String value) {
		ex.getResponseHeaders().add(name, value);
	}

	@Override
	public void setIntHeader(String name, int value) {
		setHeader(name, String.valueOf(value));
	}

	@Override
	public void addIntHeader(String name, int value) {
		addHeader(name, String.valueOf(value));
	}

	@Override
	public javax.servlet.ServletOutputStream getOutputStream() throws IOException {
		return servletOutputStream;
	}

	@Override
	public void setContentLength(int len) {
		ex.getResponseHeaders().add("Content-Length", len + "");
	}

	@Override
	public void setContentLengthLong(long len) {
		ex.getResponseHeaders().add("Content-Length", len + "");
	}

	@Override
	public void setStatus(int status) {
		this.status = status;
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
	public void sendError(int sc, String msg) throws IOException {
		this.status = sc;
		if (msg != null) {
			printWriter.write(msg);
		}
	}

	@Override
	public void sendError(int sc) throws IOException {
		sendError(sc, null);
	}

	@Override
	public void sendRedirect(String location) throws IOException {
		setHeader("Location", location);
	}

	@Override
	public void setDateHeader(String name, long date) {
		setHeader(name, Instant.ofEpochMilli(date).toString());
	}

	@Override
	public void addDateHeader(String name, long date) {
		addHeader(name, Instant.ofEpochMilli(date).toString());
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		return printWriter;
	}

	@Override
	public void setCharacterEncoding(String charset) {
		setHeader("Character-Encoding", charset);
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
	public String getHeader(String name) {
		return ex.getResponseHeaders().getFirst(name);
	}

	public void complete() throws IOException {
		try {
			printWriter.flush();

			if (outputStream.size() > 0) {
				ex.sendResponseHeaders(status, outputStream.size());
				ex.getResponseBody().write(outputStream.toByteArray());
				ex.getResponseBody().flush();
			} else {
				ex.sendResponseHeaders(status, -1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ex.close();
		}
	}

	@Override
	public void addCookie(Cookie cookie) {
		addHeader("Set-Cookie",
				cookie.getName()+"="+cookie.getValue()
				+(cookie.getMaxAge() > -1 ? "; Max-Age="+cookie.getMaxAge(): "")
				+(cookie.getSecure()?"; Secure":"")
				+(cookie.isHttpOnly()?"; HttpOnly":"")
				+(cookie.getPath() != null ? "; Path=" + cookie.getPath() : "")
		);
	}

	@Override
	public boolean containsHeader(String name) {
		return ex.getResponseHeaders().containsKey(name);
	}

	@Override
	public String encodeURL(String url) {
		return URLEncoder.encode(url);
	}

	@Override
	public String encodeRedirectURL(String url) {
		return encodeURL(url);
	}

	@Override
	public String encodeUrl(String url) {
		return encodeURL(url);
	}

	@Override
	public String encodeRedirectUrl(String url) {
		return encodeURL(url);
	}

	@Override
	public Collection<String> getHeaders(String name) {
		return ex.getResponseHeaders().get(name);
	}

	@Override
	public Collection<String> getHeaderNames() {
		return ex.getResponseHeaders().keySet();
	}
}
