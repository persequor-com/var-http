package io.varhttp.test;

import io.varhttp.Serializer;

import javax.inject.Inject;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Base64;

public class VarClientHttp implements VarClient {

	private String serverUrl = "";
	private final Serializer serializer;
	private String basePath = "";
	private final HttpHeaders defaultHeaders = new HttpHeaders();

	@Inject
	public VarClientHttp( Serializer serializer) {
		this.serializer = serializer;
	}

	@Override
	public VarClient withBasePath(String basePath) {
		this.basePath = basePath;
		return this;
	}


	public VarClient withServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
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
		return new VarClientRequest(defaultHeaders, serializer, apiRequest -> toResponse(apiRequest, HttpClient.post(serverUrl + basePath + path + apiRequest.parameters.toPath())));
	}

	@Override
	public VarClientRequest put(String path) {
		return new VarClientRequest(defaultHeaders, serializer, apiRequest -> toResponse(apiRequest, HttpClient.put(serverUrl + basePath + path + apiRequest.parameters.toPath())));
	}

	@Override
	public VarClientRequest get(String path) {
		return new VarClientRequest(defaultHeaders, serializer, apiRequest -> toResponse(apiRequest, HttpClient.get(serverUrl + basePath + path + apiRequest.parameters.toPath(), "")));
	}

	@Override
	public VarClientRequest delete(String path) {
		return new VarClientRequest(defaultHeaders, serializer, apiRequest -> toResponse(apiRequest, HttpClient.delete(serverUrl + basePath + path + apiRequest.parameters.toPath())));
	}

	@Override
	public VarClientRequest head(String path) {
		return new VarClientRequest(defaultHeaders, serializer, apiRequest -> toResponse(apiRequest, HttpClient.head(serverUrl + basePath + path + apiRequest.parameters.toPath())));
	}

	private HttpResponse toResponse(VarClientRequest varClientRequest, HttpURLConnection conn) throws IOException {
		varClientRequest.headers.forEach((name, values) -> {
					for (String value : values) {
						conn.addRequestProperty(name, value);
					}
				}
		);

		if(varClientRequest.content!=null && !varClientRequest.content.isEmpty()){
			conn.setDoOutput(true);
			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
			out.writeBytes(varClientRequest.content);
			out.flush();
			out.close();
		}

		HttpResponse httpResponse = new HttpResponse();

		httpResponse.setContent(HttpClient.readDownloadableContent(conn));

		httpResponse.setContentType(conn.getContentType());
		httpResponse.setContentEncoding(conn.getContentEncoding());
		HttpHeaders headers = new HttpHeaders();
		HttpClient.readHeaders(conn).entrySet().stream().filter(h -> h.getKey()!=null).forEach(h -> headers.add(h.getKey(), h.getValue()));

		httpResponse.setHeaders(headers);
		httpResponse.setStatusCode(conn.getResponseCode());
		return httpResponse;
	}
}
