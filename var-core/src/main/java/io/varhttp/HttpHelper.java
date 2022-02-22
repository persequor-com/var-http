package io.varhttp;

import com.google.common.base.Charsets;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

public class HttpHelper {

	public static Map<String, List<String>> decodeRequestParameters(Map<String, String[]> queryString) {
		return queryString
				.entrySet()
				.stream()
				.collect(Collectors.toMap(
						entry -> silentDecode(entry.getKey()),
						entry -> Stream.of(entry.getValue()).map(HttpHelper::silentDecode).collect(toList())));
	}

	public static List<String> decodeRequestParameters(String[] queryString) {
		return Stream.of(queryString)
				.map(HttpHelper::silentDecode)
				.collect(toList());
	}

	public static String encode(String value) {
		return silentEncode(value);
	}

	public static String decode(String value) {
		return silentDecode(value);
	}

	private static String silentDecode(String input) {
		try {
			return URLDecoder.decode(input, Charsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("Unable to decode part of query string", e);
		}
	}

	private static String silentEncode(String input) {
		try {
			return URLEncoder.encode(input, Charsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("Unable to encode part of query string", e);
		}
	}


	public static Map<String, List<String>> parseQueryString(String queryString) {
		if (queryString == null) {
			return Collections.emptyMap();
		}
		return Stream.of(queryString
						.split("&")).map(s -> s.split("=", 3))
				.filter(keyValue -> keyValue.length == 2)
				.collect(groupingBy(keyValue -> keyValue[0], mapping(keyValue -> keyValue[1], toList())));
	}

	public static Map<String, List<String>> parseQueryStringDecoded(String queryString) {
		if (queryString == null) {
			return Collections.emptyMap();
		}
		return Stream.of(queryString
						.split("&")).map(s -> s.split("=", 3))
				.filter(keyValue -> keyValue.length == 2)
				.collect(groupingBy(keyValue -> silentDecode(keyValue[0]), mapping(keyValue -> silentDecode(keyValue[1]), toList())));
	}

}
