package io.varhttp;

import java.util.HashMap;
import java.util.Map;

public class ExceptionRegistry {
	private Map<Class<? extends Throwable>, ControllerExceptionMapper> responseCodeMap = new HashMap<>();

	public void registerException(ControllerExceptionMapper handler) {
		responseCodeMap.put(handler.getClazz(), handler);
	}

	public int getResponseCode(Class<? extends Throwable> exceptionClass, int defaultResponseCode) {
		if (responseCodeMap.containsKey(exceptionClass)) {
			return responseCodeMap.get(exceptionClass).getHttpResponseCode();
		}
		return defaultResponseCode;
	}
}
