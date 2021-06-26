package io.varhttp;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * Carrier for http request headers to be injected into extension point controller methods, to allow the methods
 * to gain access to the request headers
 * </p>
 * <pre><b>Example:</b>
 *  <code>@Controller(path = "/myPath")</code>{@code
 *  public void myControllerMethod(RequestHeader requestHeader) {
 *      Set<String> headers = requestHeader.getHeaderNames();
 *      String acceptValues = requestHeader.getHeader("accept");
 *  }
 * }</pre>
 */
public interface RequestHeader {
	/**
	 * Get the header value(s) given the header name. If multiple headers match the name, all values will be returned comma
	 * separated
	 * @param name header name
	 * @return header value(s)
	 */
	String getHeader(String name);

	/**
	 * Get a list of header values corresponding to the given the header name
	 * @param name header name
	 * @return list of header values
	 */
	List<String> getHeaders(String name);

	/**
	 * Get the set of header names for the request
	 * @return header names
	 */
	Set<String> getHeaderNames();
}
