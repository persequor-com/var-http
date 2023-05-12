package io.varhttp;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Singleton
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

	public Set<Class<? extends Throwable>> getExceptionClasses() {
		return responseCodeMap.keySet();
	}
}
