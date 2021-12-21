package io.varhttp;

import com.google.common.base.Charsets;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

public class HttpHelper {

	public static Map<String, List<String>> parseQueryStringEncoded(String queryString) throws UnsupportedEncodingException {
		if (queryString == null) {
			return Collections.emptyMap();
		}
		return Stream.of(URLDecoder.decode(queryString, Charsets.UTF_8.toString())
						.split("&")).map(s -> s.split("="))
				.filter(keyValue -> keyValue.length == 2)
				.collect(groupingBy(keyValue -> keyValue[0].trim(), mapping(keyValue -> keyValue[1].trim(), toList())));
	}

	public static Map<String, List<String>> parseQueryString(String queryString) throws UnsupportedEncodingException {
		if (queryString == null) {
			return Collections.emptyMap();
		}
		return Stream.of(queryString.split("&")).map(s -> s.split("="))
				.filter(keyValue -> keyValue.length == 2)
				.collect(groupingBy(keyValue -> keyValue[0].trim(), mapping(keyValue -> keyValue[1].trim(), toList())));
	}

}
