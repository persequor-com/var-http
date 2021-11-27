/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2021-11-22
 */
package io.varhttp.test;

public interface VarClient {

	VarClient withBasePath(String basePath);
	VarClient withBasicAuth(String username, String password);
	VarClient clearBasicAuth();

	ApiRequest post(String path);
	ApiRequest put(String path);
	ApiRequest get(String path);
	ApiRequest delete(String path);
	ApiRequest head(String path);
}
