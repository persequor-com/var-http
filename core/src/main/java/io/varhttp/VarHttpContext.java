package io.varhttp;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import java.io.*;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

public class VarHttpContext implements HttpHandler {
	private final HttpServlet servlet;
	private final VarConfig config;

	public VarHttpContext(HttpServlet servlet, VarConfig config) {
		this.servlet = servlet;
		this.config = config;
	}

	@Override
	public void handle(HttpExchange ex) throws IOException {

//		InputStream requestBody = ex.getRequestBody();
//		byte[] inBytes = getBytes(ex.getRequestBody());
//		ex.getRequestBody().close();
//		ex.getRequestBody().close();
//		final ByteArrayInputStream newInput = new ByteArrayInputStream(inBytes);
		ex.setStreams(new VarWrappedInputStream(ex.getRequestBody()), new VarWrappedOutputStream(ex.getResponseBody()));
		InputStream newInput = ex.getRequestBody();

		InputStream is = newInput;


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
//			is.reset();
		}
		final Map<String, String[]> postData = parsePostData.entrySet().stream().collect(toMap(e -> e.getKey(), e -> e.getValue().toArray(new String[0])));

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
