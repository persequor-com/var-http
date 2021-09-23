package io.varhttp;

public interface FilterFactory {
	Object getInstance(Class<?> filterClass);
}
