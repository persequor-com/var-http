package io.varhttp;

import io.varhttp.parameterhandlers.HttpServletRequestParameterHandler;
import io.varhttp.parameterhandlers.HttpServletResponseParameterHandler;
import io.varhttp.parameterhandlers.IParameterHandler;
import io.varhttp.parameterhandlers.IParameterHandlerMatcher;
import io.varhttp.parameterhandlers.PathVariableParameterHandlerMatcher;
import io.varhttp.parameterhandlers.RequestBodyHandlerMatcher;
import io.varhttp.parameterhandlers.RequestHeaderParameterHandler;
import io.varhttp.parameterhandlers.RequestParameterHandler;
import io.varhttp.parameterhandlers.RequestParameterHandlerMatcher;
import io.varhttp.parameterhandlers.RequestParametersHandler;
import io.varhttp.parameterhandlers.ResponseHeaderParameterHandler;
import io.varhttp.parameterhandlers.ResponseStreamParameterHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.inject.Provider;
import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class VarServlet extends HttpServlet {
	Logger logger = LoggerFactory.getLogger(VarServlet.class);
	private final Provider<ParameterHandler> parameterHandlerProvider;
	private final FilterFactory filterFactory;
	private final ExecutionMap executions;
	private final String basePath;
	private final ControllerFilter controllerFilter;
	private final List<Class<? extends Filter>> defaultFilters = new ArrayList<>();

	public VarServlet(
			Provider<ParameterHandler> parameterHandlerProvider,
			FilterFactory filterFactory,
			String basePath, ControllerFilter controllerFilter) {
		this.parameterHandlerProvider = parameterHandlerProvider;
		this.filterFactory = filterFactory;
		this.controllerFilter = controllerFilter;
		this.executions = new ExecutionMap();
		this.basePath = basePath;
		parameterHandlerProvider.get().addParameterHandler(ResponseStreamParameterHandler.class);
		parameterHandlerProvider.get().addParameterHandler(ResponseHeaderParameterHandler.class);
		parameterHandlerProvider.get().addParameterHandler(RequestParametersHandler.class);
		parameterHandlerProvider.get().addParameterHandler(RequestParameterHandlerMatcher.class);
		parameterHandlerProvider.get().addParameterHandler(RequestHeaderParameterHandler.class);
		parameterHandlerProvider.get().addParameterHandler(RequestBodyHandlerMatcher.class);
		parameterHandlerProvider.get().addParameterHandler(PathVariableParameterHandlerMatcher.class);
		parameterHandlerProvider.get().addParameterHandler(HttpServletRequestParameterHandler.class);
		parameterHandlerProvider.get().addParameterHandler(HttpServletResponseParameterHandler.class);
	}

	public void addDefaultFilter(Class<? extends Filter> filter) {
		defaultFilters.add(filter);
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

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		handle(req, resp);
	}

	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		handle(req, resp);
	}

	@Override
	protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		handle(req, resp);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		handle(req, resp);
	}

	@Override
	protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		handle(req, resp);
	}

	public void handle(HttpServletRequest request, HttpServletResponse response) {
		final HttpMethod httpMethod = HttpMethod.valueOf(request.getMethod());
		String servletPath = request.getRequestURI();
		if (servletPath.contains("?")) {
			servletPath = servletPath.substring(0, servletPath.indexOf("?"));
		}
		Request r = new Request(httpMethod, servletPath);

		ControllerExecution exe = null;

		exe = executions.get(r.path.substring(1).split("/"), r.method);

		if (exe != null) {
			exe.execute(new ControllerContext(request, response));
		} else {
			// Strange error message
			response.setStatus(404);
			return;
		}

		try {
			if (!"head".equals(request.getMethod().toLowerCase())) {
				response.getOutputStream().flush();
			}
			response.setStatus(200);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void addExecution(Provider<Object> controllerImplementation, Method method, String baseUri, ExceptionRegistry exceptionRegistry, String classPath) {
		ParameterHandler parameterHandler = parameterHandlerProvider.get();

		Set<HttpMethod> httpMethods = parameterHandler.initializeHttpMethods(method);
		IParameterHandler[] args = parameterHandler.initializeHandlers(method, baseUri, classPath);
		for (HttpMethod httpMethod : httpMethods) {
			Request request = new Request(httpMethod, baseUri);
			ControllerExecution execution = new ControllerExecution(controllerImplementation, method, args, parameterHandler, exceptionRegistry, method.getAnnotation(Controller.class), getFilters(method), classPath);
			if (controllerFilter.accepts(request, execution)) {
				executions.put(request, execution);
			}
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
		List<Filter> filters = defaultFilters.stream().map(filterFactory::getInstance).collect(Collectors.toList());

		filters.addAll(filterAnnotations.stream().map(f -> {
			Filter filter = filterFactory.getInstance(f.filter.value());
			if (filter instanceof VarFilter) {
				((VarFilter) filter).init(method, f.filter, f.annotation);
			}
			return filter;
		}).collect(Collectors.toList()));

		return filters;
	}

	private Set<FilterTuple> getFilterAnnotations(Annotation[] annotations) {
		return Arrays.stream(annotations).map(annotation -> {
			if (annotation instanceof io.varhttp.Filter) {
				return new FilterTuple((io.varhttp.Filter) annotation, annotation);
			} else if (annotation.annotationType().getAnnotation(io.varhttp.Filter.class) != null) {
				return new FilterTuple(annotation.annotationType().getAnnotation(io.varhttp.Filter.class), annotation);
			} else {
				return null;
			}
		}).filter(Objects::nonNull).collect(Collectors.toCollection(LinkedHashSet::new));
	}

	public String getBasePath() {
		return basePath;
	}

	public void addParameterHandler(Class<? extends IParameterHandlerMatcher> handlerMatcher) {
		parameterHandlerProvider.get().addParameterHandler(handlerMatcher);
	}

	private static class FilterTuple {
		private final io.varhttp.Filter filter;
		private final Annotation annotation;

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
