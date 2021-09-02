package io.varhttp;

import com.sun.net.httpserver.HttpExchange;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

public class VarHttpServletResponse extends HttpServletResponseWrapper {
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

	public VarHttpServletResponse(HttpServletResponse response, HttpExchange ex) {
		super(response);
		this.ex = ex;
		printWriter = new PrintWriter(servletOutputStream);
	}

	@Override
	public void setContentType(String type) {
		ex.getResponseHeaders().add("Content-Type", type);
	}

	@Override
	public void setHeader(String name, String value) {
		ex.getResponseHeaders().add(name, value);
	}

	@Override
	public void addHeader(String name, String value) {
		ex.getResponseHeaders().add(name, value);
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
	public void setStatus(int status) {
		this.status = status;
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
	public PrintWriter getWriter() throws IOException {
		return printWriter;
	}

	@Override
	public String getContentType() {
		return ex.getResponseHeaders().getFirst("Content-Type");
	}

	public void complete() throws IOException {
		try {
			printWriter.flush();
			ex.sendResponseHeaders(status, outputStream.size());
			if (outputStream.size() > 0) {
				ex.getResponseBody().write(outputStream.toByteArray());
			}
			ex.getResponseBody().flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ex.close();
		}
	}

	@Override
	public void addCookie(Cookie cookie) {
		ex.getResponseHeaders().add("Set-Cookie",
				cookie.getName()+"="+cookie.getValue()
				+(cookie.getMaxAge() > 0 ? "; Max-Age="+cookie.getMaxAge(): "")
				+(cookie.getSecure()?"; Secure":"")
				+(cookie.isHttpOnly()?"; HttpOnly":"")
		);
	}

	@Override
	public Collection<String> getHeaders(String name) {
		return ex.getResponseHeaders().get(name);
	}
}
