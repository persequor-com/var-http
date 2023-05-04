package io.varhttp.parameterhandlers;

public class MissingParamException extends RuntimeException {
	public MissingParamException(String message) {
		super(message);
	}

	public MissingParamException(String message, Throwable cause) {
		super(message, cause);
	}

	public MissingParamException(Throwable cause) {
		super(cause);
	}

	public MissingParamException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
