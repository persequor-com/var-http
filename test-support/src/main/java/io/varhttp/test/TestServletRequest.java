package io.varhttp.test;

import io.varhttp.HttpHelper;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.ReadListener;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class TestServletRequest implements HttpServletRequest {

	private final VarClientRequest varClientRequest;
	private final String method;
	private final URL path;
	private Map<String, String[]> parameters;

	public TestServletRequest(VarClientRequest varClientRequest, String method, URL path) {
		this.varClientRequest = varClientRequest;
		this.method = method;
		this.path = path;
	}

	@Override
	public String getAuthType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Cookie[] getCookies() {
		String cookie = varClientRequest.headers.get("Cookie");
		if (cookie != null) {
			String[] cookies = cookie.split(";");
			return Arrays.stream(cookies).map(s -> s.split("=")).map(a -> new Cookie(a[0].trim(), a[1].trim())).toArray(Cookie[]::new);
		} else {
			return new Cookie[0];
		}
	}

	@Override
	public long getDateHeader(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getHeader(String name) {
		return varClientRequest.headers.get(name);
	}

	@Override
	public Enumeration<String> getHeaders(String name) {
		Collection<String> headers = varClientRequest.headers.getAll(name);
		return headers != null ? Collections.enumeration(headers) : Collections.emptyEnumeration();
	}

	@Override
	public Enumeration<String> getHeaderNames() {
		return Collections.enumeration(varClientRequest.headers.getNames());
	}

	@Override
	public int getIntHeader(String name) {
		return Integer.parseInt(getHeader(name));
	}

	@Override
	public String getMethod() {
		return method;
	}

	@Override
	public String getPathInfo() {
		return path.getPath().substring(getServletPath().length());
	}

	@Override
	public String getPathTranslated() {
		return null;
	}

	@Override
	public String getContextPath() {
		return "";
	}

	@Override
	public String getQueryString() {
		return path.getQuery();
	}

	@Override
	public String getRemoteUser() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isUserInRole(String role) {
		return false;
	}

	@Override
	public Principal getUserPrincipal() {
		return () -> "Anonymous";
	}

	@Override
	public String getRequestedSessionId() {
		throw new UnsupportedOperationException();
	}

	/*
	 * FIXME: this doesn't correspond to the servlet API since decoding the path, but follows standalone
	 */
	@Override
	public String getRequestURI() {
		try {
			return URLDecoder.decode(path.getPath(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public StringBuffer getRequestURL() {
		return new StringBuffer(path.getPath());
	}

	@Override
	public String getServletPath() {
		return "";
	}

	@Override
	public HttpSession getSession(boolean create) {
		throw new UnsupportedOperationException();
	}

	@Override
	public HttpSession getSession() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String changeSessionId() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isRequestedSessionIdFromUrl() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void login(String username, String password) throws ServletException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void logout() throws ServletException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<Part> getParts() throws IOException, ServletException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Part getPart(String name) throws IOException, ServletException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getAttribute(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getCharacterEncoding() {
		return "";
	}

	@Override
	public void setCharacterEncoding(String env) throws UnsupportedEncodingException {

	}

	@Override
	public int getContentLength() {
		return getIntHeader("Content-Length");
	}

	@Override
	public long getContentLengthLong() {
		return Long.parseLong(getHeader("Content-Length"));
	}

	@Override
	public String getContentType() {
		return getHeader("Content-Type");
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(varClientRequest.content.getBytes());
		return new ServletInputStream() {
			public boolean isFinished() {
				return inputStream.available() == 0;
			}

			public boolean isReady() {
				return inputStream.available() > 0;
			}

			public void setReadListener(ReadListener readListener) {
				throw new RuntimeException("?");
			}

			public int read() throws IOException {
				return inputStream.read();
			}
		};
	}

	@Override
	public String getParameter(String name) {
		String[] params = buildParameters().get(name);
		return params == null ? null : params[0];
	}

	private Map<String, String[]> buildParameters() {
		try {
			if (parameters == null) {
				Map<String, List<String>> parsedParameters = new HashMap<>();
				if (getQueryString() != null) {
					parsedParameters.putAll(HttpHelper.parseQueryString(getQueryString()));

				}
				if ("application/x-www-form-urlencoded".equals(getContentType())) {
					parsedParameters.putAll(HttpHelper.parseQueryString(varClientRequest.content));
				}
				varClientRequest.parameters.map.forEach((key, values) -> parsedParameters.computeIfAbsent(key, k -> new ArrayList<>()).addAll(values));
				parameters = parsedParameters.entrySet().stream().collect(toMap(e -> e.getKey(), e -> e.getValue().toArray(new String[0])));
			}
			return parameters;
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Enumeration<String> getParameterNames() {
		return Collections.enumeration(buildParameters().keySet());
	}

	@Override
	public String[] getParameterValues(String name) {
		return buildParameters().get(name);
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return buildParameters();
	}

	@Override
	public String getProtocol() {
		return null;
	}

	@Override
	public String getScheme() {
		return null;
	}

	@Override
	public String getServerName() {
		return null;
	}

	@Override
	public int getServerPort() {
		return 0;
	}

	@Override
	public BufferedReader getReader() throws IOException {
		return new BufferedReader(new InputStreamReader(getInputStream()));
	}

	@Override
	public String getRemoteAddr() {
		return null;
	}

	@Override
	public String getRemoteHost() {
		return null;
	}

	@Override
	public void setAttribute(String name, Object o) {

	}

	@Override
	public void removeAttribute(String name) {

	}

	@Override
	public Locale getLocale() {
		return null;
	}

	@Override
	public Enumeration<Locale> getLocales() {
		return null;
	}

	@Override
	public boolean isSecure() {
		return false;
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String path) {
		return null;
	}

	@Override
	public String getRealPath(String path) {
		return null;
	}

	@Override
	public int getRemotePort() {
		return 0;
	}

	@Override
	public String getLocalName() {
		return null;
	}

	@Override
	public String getLocalAddr() {
		return null;
	}

	@Override
	public int getLocalPort() {
		return 0;
	}

	@Override
	public ServletContext getServletContext() {
		return null;
	}

	@Override
	public AsyncContext startAsync() throws IllegalStateException {
		return null;
	}

	@Override
	public AsyncContext startAsync(javax.servlet.ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
		return null;
	}

	@Override
	public boolean isAsyncStarted() {
		return false;
	}

	@Override
	public boolean isAsyncSupported() {
		return false;
	}

	@Override
	public AsyncContext getAsyncContext() {
		return null;
	}

	@Override
	public DispatcherType getDispatcherType() {
		return null;
	}
}
