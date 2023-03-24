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
}
