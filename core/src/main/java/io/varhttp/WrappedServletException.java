package io.varhttp;

import javax.servlet.ServletException;

public class WrappedServletException extends ServletException {
	public WrappedServletException(Throwable cause) {
		super(cause);
	}
}
