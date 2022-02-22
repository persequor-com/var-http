package io.varhttp;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class VarControllerMatcher implements ControllerMatcher {
	@Override
	public Optional<ControllerMatch> find(Method method) {
		Controller controllerAnnotation = method.getAnnotation(Controller.class);
		if (controllerAnnotation != null) {
			List<HttpMethod> httpMethods = Arrays.asList(controllerAnnotation.httpMethods());
			if (httpMethods.isEmpty()) {
				httpMethods = Arrays.asList(HttpMethod.values());
			}
			return Optional.of(new ControllerMatch(method, controllerAnnotation.path(), new HashSet<>(httpMethods), controllerAnnotation.contentType()));
		}
		return Optional.empty();
	}
}
