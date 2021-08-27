package io.varhttp;

public class VarInitializationException extends RuntimeException {
	public VarInitializationException(Exception exception) {
		super(exception);
	}

	public VarInitializationException(String s) {
		super(s);
	}
}
