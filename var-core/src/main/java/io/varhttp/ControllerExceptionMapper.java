package io.varhttp;

public class ControllerExceptionMapper {
	private final Class<? extends Throwable> clazz;
	private final int httpResponseCode;

	public ControllerExceptionMapper(Class<? extends Throwable> clazz, int httpResponseCode) {
		this.clazz = clazz;
		this.httpResponseCode = httpResponseCode;
	}

	public Class<? extends Throwable> getClazz() {
		return clazz;
	}

	public int getHttpResponseCode() {
		return httpResponseCode;
	}
}
