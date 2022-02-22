package io.varhttp.filterorder;

import io.varhttp.Filter;
import io.varhttp.FilterMethod;
import io.varhttp.VarFilter;
import io.varhttp.VarFilterChain;
import io.varhttp.controllers.withfilters.FilterCatcher;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class OrderFilter implements VarFilter {
	private FilterCatcher filterCatcher;
	protected String order;


	@Inject
	public OrderFilter(FilterCatcher filterCatcher) {
		this.filterCatcher = filterCatcher;
	}

	@FilterMethod
	public void filter(VarFilterChain filterChain) throws Exception {
		filterCatcher.add("in;"+ order);
		filterChain.proceed();
		filterCatcher.add("out;"+ order);
	}

	@Override
	public void init(Method method, Filter f, Annotation annotation) {
		if (annotation != null) {
			order = annotation.annotationType().getSimpleName();
		}
	}
}
