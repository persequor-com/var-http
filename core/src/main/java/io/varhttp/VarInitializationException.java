package io.varhttp;

import java.io.IOException;

public class VarInitializationException extends RuntimeException {
	public VarInitializationException(IOException exception) {
		super(exception);
	}
}
