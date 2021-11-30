/* Copyright (C) Persequor ApS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Persequor Development Team <partnersupport@persequor.com>, 2021-11-22
 */
package io.varhttp.test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class HttpParameters {
	Map<String, List<String>> map = new HashMap<>();

	public void add(String key, String... value) {
		map.computeIfAbsent(key, k -> new ArrayList<>()).addAll(Arrays.asList(value));
	}

	public String toPath() {
		if (map.isEmpty()) {
			return "";
		}
		return "?" + map.entrySet().stream().flatMap(e -> e.getValue().stream().map(v -> {
			try {
				return URLEncoder.encode(e.getKey(), "UTF-8") + "=" + (v == null ? "" : URLEncoder.encode(v, "UTF-8"));
			} catch (UnsupportedEncodingException exception) {
				throw new RuntimeException(exception);
			}
		})).collect(Collectors.joining("&"));
	}
}
