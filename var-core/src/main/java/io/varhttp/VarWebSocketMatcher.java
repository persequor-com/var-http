package io.varhttp;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

public class VarWebSocketMatcher implements ControllerMatcher {

	@Override
	public Optional<ControllerMatch> find(Method method) {
		WebSocket controllerAnnotation = method.getAnnotation(WebSocket.class);
		if (controllerAnnotation != null) {
			return Optional.of(new ControllerMatch(method, controllerAnnotation.path(), new HashSet<>(Arrays.asList(HttpMethod.GET, HttpMethod.POST)), null));
		}
		return Optional.empty();
	}
}
