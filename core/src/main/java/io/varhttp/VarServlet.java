package io.varhttp;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Singleton
public class VarServlet extends HttpServlet {

	private final Provider<ParameterHandler> parameterHandlerProvider;
	//maps HttpMethod to controller execution
	private final ExecutionMap executions = new ExecutionMap();
	private final Map<Request, ControllerExecution> executionsOld = new HashMap<>();

	private final FilterFactory filterFactory;

	@Inject
	public VarServlet(
			Provider<ParameterHandler> parameterHandlerProvider,
			FilterFactory filterFactory) {
		this.parameterHandlerProvider = parameterHandlerProvider;
		this.filterFactory = filterFactory;
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			handle(request, response);
		} catch (Throwable e) {
			System.out.println(e.toString());
			throw e;
		}
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		handle(request, response);
	}

	public void handle(HttpServletRequest request, HttpServletResponse response) {
		final HttpMethod httpMethod = HttpMethod.valueOf(request.getMethod());
		String servletPath = request.getRequestURI();
		if (servletPath.contains("?")) {
			servletPath = servletPath.substring(0,servletPath.indexOf("?"));
		}
		Request r = new Request(httpMethod, servletPath);

		ControllerExecution exe = null;
		// Match with path-variables?
//		for(Map.Entry<Request, ControllerExecution> e : executionsOld.entrySet()) {
//			if(e.getKey().matchPath(r.path)) {
//				exe = e.getValue();
//				break;
//			}
//		}
		exe = executions.get(r.path.substring(1).split("/"), r.method);

		if(exe != null) {
			exe.execute(new ControllerContext(request, response));
		} else {
			// Strange error message
			throw new RuntimeException("HttpMethod " + request.getMethod() + " is not allowed for " + request.getPathInfo());
		}

		try {
			response.getOutputStream().flush();
			response.setStatus(200);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void addExecution(Provider<Object> controllerImplementation, Method method, String baseUri, ExceptionRegistry exceptionRegistry) {
		ParameterHandler parameterHandler = parameterHandlerProvider.get();

		Set<HttpMethod> httpMethods = parameterHandler.initializeHttpMethods(method);
//		if (executions.keySet().stream().anyMatch(httpMethods::contains)) {
//			throw new RuntimeException(String.format("Controller with same path and HTTP method " +
//					"has been registered already. Trying to add: (%s, %s)", baseUri, method));
//		}
		Function<ControllerContext, Object>[] args = parameterHandler.initializeHandlers(method, baseUri);
		for (HttpMethod httpMethod : httpMethods) {
			executions.put(new Request(httpMethod, baseUri), new ControllerExecution(controllerImplementation, method, args, parameterHandler, exceptionRegistry, getFilters(method)));
//			executionsOld.put(new Request(httpMethod, baseUri), new ControllerExecution(controllerImplementation, method, args, parameterHandler, exceptionRegistry, serializer, getFilters(method)));
		}
	}

	private List<Filter> getFilters(Method method) {
		LinkedHashSet<FilterTuple> filters = new LinkedHashSet<>();
		replaceAll(filters, getFilterAnnotations(method.getDeclaringClass().getPackage().getAnnotations()));
		replaceAll(filters, getFilterAnnotations(method.getClass().getPackage().getAnnotations()));
		replaceAll(filters, getFilterAnnotations(method.getDeclaringClass().getAnnotations()));
		replaceAll(filters, getFilterAnnotations(method.getClass().getAnnotations()));
		replaceAll(filters, getFilterAnnotations(method.getAnnotations()));
		return getFilters(method, filters);
	}

	private void replaceAll(LinkedHashSet<FilterTuple> filters, Set<FilterTuple> filterAnnotations) {
		filters.removeAll(filterAnnotations);
		filters.addAll(filterAnnotations);
	}

	private List<Filter> getFilters(Method method, Set<FilterTuple> filterAnnotations) {
		List<Filter> filters = filterAnnotations.stream().map(f -> {
			Filter filter = filterFactory.getInstance(f.filter.value());
			if (filter instanceof VarFilter) {
				((VarFilter) filter).init(method, f.filter, f.annotation);
			}
			return filter;
		}).collect(Collectors.toList());
		return filters;
	}

	private Set<FilterTuple> getFilterAnnotations(Annotation[] annotations) {
		Set<FilterTuple> res = Arrays.stream(annotations).map(annotation -> {
			if (annotation instanceof io.varhttp.Filter) {
				return new FilterTuple((io.varhttp.Filter) annotation, annotation);
			} else if (annotation.annotationType().getAnnotation(io.varhttp.Filter.class) != null) {
				return new FilterTuple(annotation.annotationType().getAnnotation(io.varhttp.Filter.class), annotation);
			} else {
				return null;
			}
		}).filter(Objects::nonNull).collect(Collectors.toCollection(LinkedHashSet::new));
		return res;
	}

	private static class FilterTuple {
		private io.varhttp.Filter filter;
		private Annotation annotation;

		public FilterTuple(io.varhttp.Filter filter, Annotation annotation) {
			this.filter = filter;
			this.annotation = annotation;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Class<? extends Filter> otherValue = ((FilterTuple) o).filter.value();
			Class<? extends Filter> value = filter.value();
			return Objects.equals(value, otherValue);
		}

		@Override
		public int hashCode() {
			return filter.value().hashCode();
		}
	}
}
