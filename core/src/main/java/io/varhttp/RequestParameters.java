package io.varhttp;

import java.util.List;
import java.util.Map;

public interface RequestParameters {
	String get(String name);
	void remove(String name);
	boolean contains(String name);
	Map<String, List<String>> getMap();
}
