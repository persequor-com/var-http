package io.varhttp;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Optional;

public class AnnotationsHelper {
	public Annotations getCumulativeAnnotations(Method method) {
		return getAnnotations(method.getDeclaringClass().getPackage())
				.add(getAnnotations(method.getClass().getPackage()))
				.add(getAnnotations(method.getDeclaringClass()))
				.add(getAnnotations(method.getClass()))
				.add(new Annotations(method.getAnnotations()));
	}

	public Annotations getAnnotations(Method method) {
		return new Annotations(method.getAnnotations());
	}

	public Annotations getCumulativeAnnotations(Class<?> clazz) {
		return getAnnotations(clazz.getPackage()).add(new Annotations(clazz.getAnnotations()));
	}

	public Annotations getAnnotations(Class<?> clazz) {
		return new Annotations(clazz.getAnnotations());
	}

	public Annotations getAnnotations(Package pack) {
		return new Annotations(pack.getAnnotations());
	}

	public static class Annotations extends LinkedHashSet<Annotation> {
		public Annotations(Annotation[] annotations) {
			super(Arrays.asList(annotations));
		}

		public Annotations add(Annotations annotations) {
			addAll(annotations);
			return this;
		}

		@SuppressWarnings("unchecked")
		public <T> Optional<T> get(Class<T> clazz) {
			return (Optional<T>)stream().filter(a -> clazz.isAssignableFrom(a.annotationType())).findFirst();
		}
	}
}
