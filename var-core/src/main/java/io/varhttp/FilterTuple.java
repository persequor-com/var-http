package io.varhttp;

import java.lang.annotation.Annotation;
import java.util.Objects;

public class FilterTuple {
	private final Class<?> filterClass;
	private io.varhttp.Filter filter;
	private Annotation annotation;

	public FilterTuple(io.varhttp.Filter filter, Annotation annotation) {
		this.filter = filter;
		this.annotation = annotation;
		this.filterClass = filter.value();
	}

	public FilterTuple(Class<?> filterClass) {
		this.filterClass = filterClass;
	}

	public Filter getFilter() {
		return filter;
	}

	public Annotation getAnnotation() {
		return annotation;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		FilterTuple that = (FilterTuple) o;

		return Objects.equals(filterClass, that.filterClass)
				&& Objects.equals(filter, that.filter)
				&& (annotation == null || Objects.equals(annotation.getClass(), that.annotation.getClass()));
	}

	@Override
	public int hashCode() {
		return Objects.hash(filterClass, filter, annotation != null ? annotation.getClass() : null);
	}

	public Class<?> getFilterClass() {
		return filterClass;
	}
}
