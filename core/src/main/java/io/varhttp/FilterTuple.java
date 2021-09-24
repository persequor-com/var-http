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
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		FilterTuple that = (FilterTuple) o;

		if (!Objects.equals(filter.value(), that.filter.value())) return false;
		return Objects.equals(annotation.getClass(), that.annotation.getClass());
	}

	@Override
	public int hashCode() {
		int result = filter.value() != null ? filter.value().hashCode() : 0;
		result = 31 * result + (annotation.getClass() != null ? annotation.getClass().hashCode() : 0);
		return result;
	}
}
