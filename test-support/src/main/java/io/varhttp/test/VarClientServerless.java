package io.varhttp.test;

import io.varhttp.Serializer;
import io.varhttp.VarServlet;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;

public class VarClientServerless implements VarClient {

	private final VarServlet varServlet;
	private final Serializer serializer;

	private final HttpHeaders defaultHeaders = new HttpHeaders();
	private String basePath = "";

	@Inject
	public VarClientServerless(VarServlet varServlet, Serializer serializer) {
		this.varServlet = varServlet;
		this.serializer = serializer;
		defaultHeaders.put("Accept", "*/*");
	}

	@Override
	public VarClient withBasePath(String basePath) {
		this.basePath = basePath;
		return this;
	}

	@Override
	public VarClient withBasicAuth(String username, String password) {
		String encoded = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
		defaultHeaders.put("authorization", "Basic " + encoded);
		return this;
	}

	@Override
	public VarClient clearBasicAuth() {
		defaultHeaders.remove("authorization");
		return this;
	}

	@Override
	public VarClientRequest post(String path) {
		return new VarClientRequest(defaultHeaders,
				serializer, apiRequest -> executeRequest(path, "POST", apiRequest, varServlet));
	}

	@Override
	public VarClientRequest put(String path) {
		return new VarClientRequest(defaultHeaders,
				serializer, apiRequest -> executeRequest(path, "PUT", apiRequest, varServlet));
	}

	@Override
	public VarClientRequest get(String path) {
		return new VarClientRequest(defaultHeaders,
				serializer, apiRequest -> executeRequest(path, "GET", apiRequest, varServlet));
	}

	@Override
	public VarClientRequest webSocket(String path) {
		throw new RuntimeException("Websockets does not yet work in serverless tests");
	}

	@Override
	public VarClientRequest delete(String path) {
		return new VarClientRequest(defaultHeaders,
				serializer, apiRequest -> executeRequest(path, "DELETE", apiRequest, varServlet));
	}

	@Override
	public VarClientRequest head(String path) {
		return new VarClientRequest(defaultHeaders,
				serializer, apiRequest -> executeRequest(path, "HEAD", apiRequest, varServlet));
	}

	private HttpResponse executeRequest(String path, String method, VarClientRequest varClientRequest, VarServlet varServlet) throws IOException {
		HttpServletRequest servletRequest = new TestServletRequest(varClientRequest, method, new URL("https://component-test-host" + basePath + path));
		TestServletResponse servletResponse = new TestServletResponse();
		varServlet.handle(servletRequest, servletResponse);
		return toHttpResponse(servletResponse);
	}

	private HttpResponse toHttpResponse(TestServletResponse testServletResponse) throws IOException {
		HttpResponse httpResponse = new HttpResponse();
		httpResponse.setContent(testServletResponse.outputStream.toString());
		httpResponse.setContentType(testServletResponse.getContentType());
		httpResponse.setContentEncoding(testServletResponse.getCharacterEncoding());
		HttpHeaders headers = new HttpHeaders();
		for (String headerName : testServletResponse.getHeaderNames()) {
			headers.add(headerName, testServletResponse.getHeaders(headerName));
		}

		httpResponse.setHeaders(headers);
		httpResponse.setStatusCode(testServletResponse.getStatus());
		return httpResponse;
	}

}
