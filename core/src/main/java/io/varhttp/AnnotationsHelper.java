package io.varhttp;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;

public class AnnotationsHelper {
	Annotations getCumulativeAnnotations(Method method) {
		return getAnnotations(method.getDeclaringClass().getPackage())
				.add(getAnnotations(method.getClass().getPackage()))
				.add(getAnnotations(method.getDeclaringClass()))
				.add(getAnnotations(method.getClass()))
				.add(new Annotations(method.getAnnotations()));
	}

	Annotations getAnnotations(Method method) {
		return new Annotations(method.getAnnotations());
	}

	Annotations getCumulativeAnnotations(Class<?> clazz) {
		return getAnnotations(clazz.getPackage()).add(new Annotations(clazz.getAnnotations()));
	}

	Annotations getAnnotations(Class<?> clazz) {
		return new Annotations(clazz.getAnnotations());
	}

	Annotations getAnnotations(Package pack) {
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
	}
}
