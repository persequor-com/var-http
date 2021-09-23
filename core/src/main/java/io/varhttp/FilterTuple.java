package io.varhttp;

import java.lang.annotation.Annotation;
import java.util.Objects;

public class FilterTuple {
	private final io.varhttp.Filter filter;
	private final Annotation annotation;

	public FilterTuple(io.varhttp.Filter filter, Annotation annotation) {
		this.filter = filter;
		this.annotation = annotation;
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

		Class<?> otherValue = ((FilterTuple) o).filter.value();
		Class<?> value = filter.value();
		return Objects.equals(value, otherValue);
	}

	@Override
	public int hashCode() {
		return filter.value().hashCode();
	}
}
