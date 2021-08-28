package io.varhttp;

public interface RequestParameters {
	String get(String name);
	void remove(String name);
	boolean contains(String name);
}
