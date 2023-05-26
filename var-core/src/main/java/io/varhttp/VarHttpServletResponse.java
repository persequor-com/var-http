package io.varhttp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;

public class VarHttpServletResponse extends HttpServletResponseWrapper {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public VarHttpServletResponse(HttpServletResponse response) {
		super(response);
	}

	@Override
	public void setStatus(int sc) {
		throwIfComplete();
		super.setStatus(sc);
	}

	@Override
	public void addCookie(Cookie cookie) {
		throwIfComplete();
		super.addCookie(cookie);
	}

	@Override
	public void addHeader(String name, String value) {
		throwIfComplete();
		super.addHeader(name, value);
	}

	@Override
	public void setContentType(String type) {
		throwIfComplete();
		super.setContentType(type);
	}


	private void throwIfComplete() {
		if (this.isCommitted()) {
			throw new IllegalStateException("The response has been committed. " +
					"A committed response has already had its status code and headers written. " +
					"So, no changes of those response properties are allowed anymore.");
		}
	}


	private final VarServletOutputStream servletStream = new VarServletOutputStream();

	public void complete() {
		try {
			servletStream.asPrintWriter().flush();

			if (servletStream.getInternalBuffer().size() > 0) {
				super.setContentLength(servletStream.getInternalBuffer().size());
				super.getOutputStream().write(servletStream.getInternalBuffer().toByteArray());
				super.flushBuffer();
			}
		} catch (IOException e) {
			logger.debug("Couldn't write response due to client closing the connection.", e);
		} catch (RuntimeException e) {
			logger.debug("Runtime exception.", e);
		}
	}

	@Override
	public void flushBuffer() throws IOException {
		servletStream.flush();
	}

	@Override
	public void sendError(int sc, String msg) throws IOException {
		setStatus(sc);
		if (msg != null) {
			this.getWriter().write(msg);
		}
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		return servletStream.asPrintWriter();
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return servletStream;
	}
}
