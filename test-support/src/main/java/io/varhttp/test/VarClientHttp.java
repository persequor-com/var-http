/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>,
 */
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
	public ApiRequest post(String path) {
		return new ApiRequest(defaultHeaders, serializer, apiRequest -> toResponse(apiRequest, HttpClient.post(serverUrl + basePath + path + apiRequest.parameters.toPath())));
	}

	@Override
	public ApiRequest put(String path) {
		throw new UnsupportedOperationException("not yet implmenented");
	}

	@Override
	public ApiRequest get(String path) {
		return new ApiRequest(defaultHeaders, serializer, apiRequest -> toResponse(apiRequest, HttpClient.get(serverUrl + basePath + path + apiRequest.parameters.toPath(), "")));
	}

	@Override
	public ApiRequest delete(String path) {
		throw new UnsupportedOperationException("not yet implmenented");
	}

	@Override
	public ApiRequest head(String path) {
		return new ApiRequest(defaultHeaders, serializer, apiRequest -> toResponse(apiRequest, HttpClient.head(serverUrl + basePath + path + apiRequest.parameters.toPath())));
	}

	private HttpResponse toResponse(ApiRequest apiRequest, HttpURLConnection conn) throws IOException {
		apiRequest.headers.forEach((name, values) -> {
					for (String value : values) {
						conn.addRequestProperty(name, value);
					}
				}
		);

		if(apiRequest.content!=null && !apiRequest.content.isEmpty()){
			conn.setDoOutput(true);
			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
			out.writeBytes(apiRequest.content);
			out.flush();
			out.close();
		}

		HttpResponse httpResponse = new HttpResponse();

		httpResponse.setContent(HttpClient.readContent(conn).toString());

		httpResponse.setContentType(conn.getContentType());
		httpResponse.setContentEncoding(conn.getContentEncoding());
		HttpHeaders headers = new HttpHeaders();
		HttpClient.readHeaders(conn).entrySet().stream().filter(h -> h.getKey()!=null).forEach(h -> headers.add(h.getKey(), h.getValue()));

		httpResponse.setHeaders(headers);
		httpResponse.setStatusCode(conn.getResponseCode());
		return httpResponse;
	}
}
