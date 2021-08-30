package io.varhttp;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class VarHttpContext implements HttpHandler {
	private final HttpServlet servlet;

	public VarHttpContext(HttpServlet servlet) {
		this.servlet = servlet;
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
				parsePostData.putAll(HttpUtils.parseQueryString(ex.getRequestURI().getQuery()));
			}

			// check if any postdata to parse
			parsePostData.putAll(HttpUtils.parsePostData(inBytes.length, is));
		} catch (IllegalArgumentException e) {
			// no postData - just reset inputstream
		} finally {
			newInput.reset();
		}
		final Map<String, String[]> postData = parsePostData;

		VarHttpServletRequest req = new VarHttpServletRequest(createUnimplementAdapter(HttpServletRequest.class), ex, postData, is);

		VarHttpServletResponse resp = new VarHttpServletResponse(createUnimplementAdapter(HttpServletResponse.class), ex);

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

	@SuppressWarnings("unchecked")
	private static <T> T createUnimplementAdapter(Class<T> httpServletApi) {
		class UnimplementedHandler implements InvocationHandler {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				System.out.println("Not implemented: " + method + ", args=" + Arrays.toString(args));
				throw new UnsupportedOperationException("Not implemented: " + method + ", args=" + Arrays.toString(args));
			}
		}

		return (T) Proxy.newProxyInstance(UnimplementedHandler.class.getClassLoader(),
				new Class<?>[] { httpServletApi },
				new UnimplementedHandler());
	}
}
