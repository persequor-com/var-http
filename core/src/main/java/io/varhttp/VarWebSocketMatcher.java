package io.varhttp;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

public class VarWebSocketMatcher implements ControllerMatcher {
	private RegisteredWebSockets registeredWebSockets;

	public VarWebSocketMatcher(RegisteredWebSockets registeredWebSockets) {
		this.registeredWebSockets = registeredWebSockets;
	}

	@Override
	public Optional<ControllerMatch> find(Method method) {
		WebSocket controllerAnnotation = method.getAnnotation(WebSocket.class);
		if (controllerAnnotation != null) {
			return Optional.of(new ControllerMatch(method, controllerAnnotation.path(), new HashSet<>(Arrays.asList(HttpMethod.GET, HttpMethod.POST)), null));
		}
		return Optional.empty();
	}
}
