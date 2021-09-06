package io.varhttp;

import io.varhttp.controllers.AltControllerAnnotation;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

public class AltControllerMatcher implements ControllerMatcher {
	@Override
	public Optional<ControllerMatch> find(Method method) {
		AltControllerAnnotation annotation = method.getAnnotation(AltControllerAnnotation.class);
		if (annotation != null) {
			return Optional.of(new ControllerMatch(method, annotation.urlPath(), new HashSet<>(Arrays.asList(HttpMethod.GET)), ""));
		}
		return Optional.empty();
	}
}
