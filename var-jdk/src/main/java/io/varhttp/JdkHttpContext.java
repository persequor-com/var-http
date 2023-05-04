package io.varhttp;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.*;

public class JdkHttpContext implements HttpHandler {
	private final HttpServlet servlet;
	private final VarConfig config;

	public JdkHttpContext(HttpServlet servlet, VarConfig config) {
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

		Map<String, List<String>> parsePostData = new HashMap<>();

		try {
			if (ex.getRequestURI().getQuery() != null) {
				parsePostData.putAll(HttpHelper.parseQueryString(ex.getRequestURI().getRawQuery()));
			}

			// check if any postdata to parse
			if ("application/x-www-form-urlencoded".equals(ex.getRequestHeaders().getFirst("Content-Type"))) {
				String wwwForm = CharStreams.toString(new InputStreamReader(is, Charsets.UTF_8));
				parsePostData.putAll(HttpHelper.parseQueryString(wwwForm));
			}
		} finally {
			newInput.reset();
		}
		final Map<String, String[]> postData = parsePostData.entrySet().stream().collect(toMap(e -> e.getKey(), e -> e.getValue().toArray(new String[0])));

		JdkHttpServletRequest req = new JdkHttpServletRequest(ex, postData, is, new JdkServletContext(ex), config);

		JdkHttpServletResponse resp = new JdkHttpServletResponse(ex);

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
