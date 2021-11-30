package io.varhttp.test;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

class HttpHeaders {

	private final Map<String, List<String>> headers = new HashMap<>();

	public void put(String name, String value) {
		ArrayList<String> list = new ArrayList<>();
		list.add(value);
		headers.put(name.toLowerCase(), list);
	}

	public void add(String name, Collection<String> value) {
		headers.computeIfAbsent(name.toLowerCase(), k -> new ArrayList<>()).addAll(value);
	}

	public void add(String name, String singleValue) {
		headers.computeIfAbsent(name.toLowerCase(), k -> new ArrayList<>()).add(singleValue);
	}

	public void add(HttpHeaders otherHeaders) {
		otherHeaders.headers.forEach(this::add);
	}

	public void forEach(BiConsumer<String, List<String>> consumer) {
		headers.forEach(consumer);
	}

	public void putIfNotPresent(String name, List<String> value) {
		if (!headers.containsKey(name.toLowerCase())) {
			add(name, value);
		}
	}

	public String get(String name) {
		List<String> value = headers.get(name.toLowerCase());
		return value == null ? null : value.get(0);
	}

	public List<String> getAll(String key) {
		return headers.get(key.toLowerCase());
	}

	public Set<String> getNames() {
		return headers.keySet();
	}

	public void remove(String name) {
		headers.remove(name);
	}
}
