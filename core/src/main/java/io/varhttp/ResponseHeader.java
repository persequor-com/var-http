package io.varhttp;

import java.net.URL;
import javax.servlet.http.Cookie;

/**
 * <p>
 * Carrier for http response headers to be injected into extension point controller methods, to allow the methods
 * to gain access to the response headers
 * </p>
 * <pre><b>Example:</b>
 *  <code>@Controller(path = "/myPath")</code>{@code
 *  public void myControllerMethod(ResponseHeader responseHeader) {
 *      responseHeader.addHeader("myHeaderName", "myHeaderValue");
 *      responseHeader.setStatus(200);
 *  }
 * }</pre>
 */
public interface ResponseHeader {
	/**
	 * Explicitly set the http response code
	 * @param httpResponseCode response code, e.g. 200 for "OK"
	 */
	void setStatus(int httpResponseCode);

	/**
	 * Explicitly add a http header to the response
	 * @param name the header name
	 * @param value the header value
	 */
	void addHeader(String name, String value);

	/**
	 * Explicitly set (override) a http header to the response
	 * @param name the header name
	 * @param value the header value
	 */
	void setHeader(String name, String value);

	/**
	 * Explicitly add cookie to the response
	 * @param cookie the cookie
	 */
	void addCookie(Cookie cookie);

	/**
	 * @param path relative to the root of the domain
	 */
	void redirect(String path);

	/**
	 * @param path relative to this controller class
	 */
	void redirectRelative(String path);

	/**
	 * @param url
	 */
	void redirect(URL url);

	default void setContentType(String s) {
		setHeader("Content-Type", s);
	}
}
