package io.varhttp;

import com.sun.net.httpserver.HttpExchange;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class VarHttpServletRequest extends HttpServletRequestWrapper {
	private final HttpExchange ex;
	private final Map<String, String[]> postData;
	private final ServletInputStream is;
	private final Map<String, Object> attributes = new HashMap<>();

	public VarHttpServletRequest(HttpServletRequest request, HttpExchange ex, Map<String, String[]> postData, ServletInputStream is) {
		super(request);
		this.ex = ex;
		this.postData = postData;
		this.is = is;
	}

	@Override
	public String getHeader(String name) {
		return ex.getRequestHeaders().getFirst(name);
	}

	@Override
	public Enumeration<String> getHeaders(String name) {
		if (ex.getRequestHeaders().get(name) == null) {
			return new Vector<String>(0).elements();
		}
		return new Vector<String>(ex.getRequestHeaders().get(name)).elements();
	}

	@Override
	public Enumeration<String> getHeaderNames() {
		return new Vector<String>(ex.getRequestHeaders().keySet()).elements();
	}

	@Override
	public Object getAttribute(String name) {
		return attributes.get(name);
	}

	@Override
	public void setAttribute(String name, Object o) {
		this.attributes.put(name, o);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return new Vector<String>(attributes.keySet()).elements();
	}

	@Override
	public String getMethod() {
		return ex.getRequestMethod();
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		return is;
	}

	@Override
	public BufferedReader getReader() throws IOException {
		return new BufferedReader(new InputStreamReader(getInputStream()));
	}

	@Override
	public String getPathInfo() {
		return ex.getRequestURI().getPath();
	}

	@Override
	public String getParameter(String name) {
		String[] arr = postData.get(name);
		return arr != null ? (arr.length > 1 ? Arrays.toString(arr) : arr[0]) : null;
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return postData;
	}

	@Override
	public Enumeration<String> getParameterNames() {
		return new Vector<String>(postData.keySet()).elements();
	}

	public String[] getParameterValues(String name) {
		return postData.get(name);
	}

	@Override
	public String getRequestURI() {
		return ex.getRequestURI().toString();
	}

	@Override
	public String getContentType() {
		return ex.getRequestHeaders().getFirst("Content-Type");
	}

	@Override
	public String getServletPath() {
		String path = ex.getHttpContext().getPath().substring(1);

		return path;
	}
}
