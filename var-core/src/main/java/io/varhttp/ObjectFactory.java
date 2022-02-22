package io.varhttp;

public interface ObjectFactory {
	<T> T getInstance(Class<T> tClass);
}
