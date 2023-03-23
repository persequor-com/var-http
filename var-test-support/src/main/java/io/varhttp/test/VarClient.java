package io.varhttp.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;

public interface VarClient {

	VarClient withBasePath(String basePath);
	VarClient withBasicAuth(String username, String password);
	VarClient clearBasicAuth();

	VarClientRequest post(String path);
	VarClientRequest put(String path);
	VarClientRequest get(String path);
	VarClientRequest delete(String path);
	VarClientRequest head(String path);

	static HttpResponse getHttpResponseWithContent(VarClientRequest.ContentFormat contentFormat, HttpURLConnection conn) throws IOException {
		HttpResponse httpResponse = new HttpResponse();

		switch (contentFormat) {
			case STREAM_CONTENT:
				httpResponse.setInputStream(HttpClient.readDownloadableContent(conn));
				break;
			case STRING_CONTENT:
				httpResponse.setContent(HttpClient.readContent(conn).toString());
				break;
			default:
		}
		return httpResponse;
	}

	static HttpResponse getHttpResponseWithContent(VarClientRequest.ContentFormat contentFormat, TestServletResponse testServletResponse) throws IOException {
		HttpResponse httpResponse = new HttpResponse();
		switch (contentFormat) {
			case STREAM_CONTENT:
				httpResponse.setInputStream(new ByteArrayInputStream(testServletResponse.outputStream.toByteArray()));
				break;
			case STRING_CONTENT:
				httpResponse.setContent(testServletResponse.outputStream.toString());
				break;
			default:
		}
		return httpResponse;
	}

}
