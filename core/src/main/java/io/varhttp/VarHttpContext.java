package io.varhttp;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpUtils;

public class VarHttpContext implements HttpHandler {
	private final HttpServlet servlet;
	private final VarConfig config;

	public VarHttpContext(HttpServlet servlet, VarConfig config) {
		this.servlet = servlet;
		this.config = config;
	}

	@Override
	public void handle(HttpExchange ex) throws IOException {
		byte[] inBytes = getBytes(ex.getRequestBody());
		ex.getRequestBody().close();
		final ByteArrayInputStream newInput = new ByteArrayInputStream(inBytes);
		final ServletInputStream is = new ServletInputStream() {

			@Override
			public boolean isFinished() {
				return newInput.available() == 0;
			}

			@Override
			public boolean isReady() {
				return newInput.available() > 0;
			}

			@Override
			public void setReadListener(ReadListener readListener) {
				throw new RuntimeException("?");
			}

			@Override
			public int read() throws IOException {
				return newInput.read();
			}
		};

		Map<String, String[]> parsePostData = new HashMap<>();

		try {
			if (ex.getRequestURI().getQuery() != null) {
				parsePostData.putAll(HttpUtils.parseQueryString(ex.getRequestURI().getRawQuery()));
			}

			// check if any postdata to parse
			if ("application/x-www-form-urlencoded".equals(ex.getRequestHeaders().getFirst("Content-Type"))) {
				parsePostData.putAll(HttpUtils.parsePostData(inBytes.length, is));
			}
		} finally {
			newInput.reset();
		}
		final Map<String, String[]> postData = parsePostData;

		VarHttpServletRequest req = new VarHttpServletRequest(ex, postData, is, new VarServletContext(ex), config);

		VarHttpServletResponse resp = new VarHttpServletResponse(ex);

		try {
			servlet.service(req, resp);
			resp.complete();
		} catch (ServletException e) {
			throw new IOException(e);
		}
	}

	private static byte[] getBytes(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		while (true) {
			int r = in.read(buffer);
			if (r == -1)
				break;
			out.write(buffer, 0, r);
		}
		return out.toByteArray();
	}
}
