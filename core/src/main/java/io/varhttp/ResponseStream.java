package io.varhttp;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * <p>
 * Carrier for http response body stream to be injected into extension point controller methods, to allow the methods
 * to gain access to the response body stream
 * </p>
 * <pre><b>Example:</b>
 *  <code>@Controller(path = "/myPath")</code>{@code
 *  public void myControllerMethod(ResponseStream responseStream) {
 *  	try (BufferedWriter writer = responseStream.getContentWriter("fileName.csv", "text/csv", StandardCharsets.UTF_8)) {
 * 			writer.append("fileContent");
 * 		}
 *  }
 * }</pre>
 */
public interface ResponseStream {
	/**
	 * Get writer that writes to response body in streaming way. This should trigger file download in browser.
	 *
	 * @param fileName    Name of file for download eg. name.csv
	 * @param contentType File format eg. text/csv for csv files
	 * @param charset     Character encoding of the file content eg. StandardCharsets.UTF-8 for UTF-8 encoding
	 * @return writer that writes to response body
	 */
	BufferedWriter getContentWriter(String fileName, String contentType, Charset charset);

	/**
	 * Get the output-stream of the response. Used to write non-string content.
	 *
	 * @param contentType Content format eg. image/png for PNG files
	 * @param charset     Character encoding of the file content eg. StandardCharsets.UTF-8 for UTF-8 encoding
	 * @return the output stream of the response.
	 */
	OutputStream getOutputStream(String contentType, Charset charset);

	default OutputStream getOutputStream(String contentType) { return getOutputStream(contentType, null); }

	void write(Object content);

	void write(Object content, String contentType);
}
