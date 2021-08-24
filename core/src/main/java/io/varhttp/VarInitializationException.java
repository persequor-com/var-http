package io.varhttp;

public class VarInitializationException extends RuntimeException {
	public VarInitializationException(Exception exception) {
		super(exception);
	}
}
