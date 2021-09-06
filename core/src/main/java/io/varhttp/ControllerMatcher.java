package io.varhttp;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

public interface ControllerMatcher {
	Optional<ControllerMatch> find(Method method);
}
