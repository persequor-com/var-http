package io.varhttp;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpsServer;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

public class VarHttpServletRequest implements HttpServletRequest {

	private final HttpExchange ex;
	private final Map<String, String[]> postData;
	private final ServletInputStream is;
	private final Map<String, Object> attributes = new HashMap<>();
	private final ServletContext context;
	private final VarConfig config;

	public VarHttpServletRequest(HttpExchange ex, Map<String, String[]> postData, ServletInputStream is, ServletContext context, VarConfig config) {
		this.ex = ex;
		this.postData = postData;
		this.is = is;
		this.context = context;
		this.config = config;
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
	public int getIntHeader(String name) {
		return Integer.parseInt(getHeader(name));
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
	public void removeAttribute(String name) {
		attributes.remove(name);
	}

	@Override
	public Locale getLocale() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Enumeration<Locale> getLocales() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isSecure() {
		if(config.isForceRequestSecure()) {
			return config.isForceRequestSecure();
		}

		if(getHeader("X-Forwarded-Proto") != null) {
			return getHeader("X-Forwarded-Proto").equalsIgnoreCase("https");
		}

		return (ex.getHttpContext().getServer() instanceof HttpsServer);
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String path) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getRealPath(String path) {
		return null;
	}

	@Override
	public int getRemotePort() {
		return ex.getRemoteAddress().getPort();
	}

	@Override
	public String getLocalName() {
		return ex.getLocalAddress().getHostName();
	}

	@Override
	public String getLocalAddr() {
		return ex.getLocalAddress().getAddress().getCanonicalHostName();
	}

	@Override
	public int getLocalPort() {
		return ex.getLocalAddress().getPort();
	}

	@Override
	public ServletContext getServletContext() {
		return context;
	}

	@Override
	public AsyncContext startAsync() throws IllegalStateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
		throw new UnsupportedOperationException();
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
		throw new UnsupportedOperationException();
	}

	@Override
	public DispatcherType getDispatcherType() {
		return DispatcherType.REQUEST;
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return new Vector<String>(attributes.keySet()).elements();
	}

	@Override
	public String getCharacterEncoding() {
		return ex.getResponseHeaders().getFirst("Character-Encoding");
	}

	@Override
	public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
		ex.getRequestHeaders().set("Character-Encoding", env);
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
	public String getRemoteAddr() {
		return ex.getRemoteAddress().getAddress().getHostAddress();
	}

	@Override
	public String getRemoteHost() {
		return ex.getRemoteAddress().getAddress().getHostName();
	}

	@Override
	public String getPathInfo() {
		return ex.getRequestURI().getPath().substring(getServletPath().length());
	}

	@Override
	public String getPathTranslated() {
		return null;
	}

	//In standalone mode all the servlets are run within the root context
	@Override
	public String getContextPath() {
		return "";
	}

	@Override
	public String getQueryString() {
		return ex.getRequestURI().getQuery();
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
	public String getProtocol() {
		return ex.getProtocol();
	}

	@Override
	public String getScheme() {
		return isSecure() ? "https" : "http";
	}

	@Override
	public String getServerName() {
		return ex.getLocalAddress().getHostName();
	}

	@Override
	public int getServerPort() {
		return ex.getLocalAddress().getPort();
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
		return ex.getRequestURI().getPath();
	}

	@Override
	public StringBuffer getRequestURL() {
		return new StringBuffer(getScheme()).append(":/").append(ex.getLocalAddress()).append(ex.getRequestURI());
	}

	@Override
	public String getContentType() {
		return ex.getRequestHeaders().getFirst("Content-Type");
	}

	@Override
	public String getServletPath() {
		String contextPath = ex.getHttpContext().getPath();
		//will return empty string for root context which is correct according to spec
		return contextPath.endsWith("/") ? contextPath.substring(0, contextPath.length() - 1) : contextPath;
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
	public String getAuthType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Cookie[] getCookies() {
		String cookieString = ex.getRequestHeaders().getFirst("Cookie");
		if (cookieString == null) {
			return new Cookie[0];
		}
		String[] cookies = cookieString.split(";");
		return Arrays.stream(cookies).map(s -> s.split("=")).map(a -> new Cookie(a[0].trim(), a[1].trim())).toArray(Cookie[]::new);
	}

	@Override
	public long getDateHeader(String name) {
		throw new UnsupportedOperationException();
	}
}
