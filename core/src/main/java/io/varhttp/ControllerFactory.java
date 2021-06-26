package io.varhttp;

public interface ControllerFactory {
	Object getInstance(Class<?> controllerClass);
}
