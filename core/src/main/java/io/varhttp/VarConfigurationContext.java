package io.varhttp;

import io.varhttp.parameterhandlers.IParameterHandler;
import io.varhttp.parameterhandlers.IParameterHandlerMatcher;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.servlet.Filter;

public class VarConfigurationContext {
	private VarServlet varServlet;
	VarConfigurationContext parentContext;
	ParameterHandler parameterHandler = null;
	ControllerFilter controllerFilter = null;
	ExceptionRegistry exceptionRegistry = null;
	ControllerFactory controllerFactory = null;
	FilterFactory filterFactory = null;
	List<ControllerMatcher> controllerMatchers = new ArrayList<>();
	String basePath = "";
	List<Class<? extends Filter>> defaultFilters = new ArrayList<>();



	public VarConfigurationContext(VarServlet varServlet, VarConfigurationContext parentContext, ParameterHandler parameterHandler) {
		this.varServlet = varServlet;
		this.parameterHandler = parameterHandler;
		this.parentContext = parentContext;
	}
	
	ParameterHandler getParameterHandler() {
		if (parameterHandler == null) {
			return parentContext.getParameterHandler();
		}
		return parameterHandler;
	}


	FilterFactory getFilterFactory() {
		if (filterFactory == null) {
			return parentContext.getFilterFactory();
		}
		return filterFactory;
	}

	ControllerFilter getControllerFilter() {
		if (controllerFilter == null) {
			return parentContext.getControllerFilter();
		}
		return controllerFilter;
	}

	ExceptionRegistry getExceptionRegistry() {
		if (exceptionRegistry == null) {
			return parentContext.getExceptionRegistry();
		}
		return exceptionRegistry;
	}

	ControllerFactory getControllerFactory() {
		if (controllerFactory == null) {
			return parentContext.getControllerFactory();
		}
		return controllerFactory;
	}


	public List<ControllerMatcher> getControllerMatchers() {
		return Stream.concat(controllerMatchers.stream(),parentContext.getControllerMatchers().stream()).collect(Collectors.toList());
	}


	Collection<Class<? extends Filter>> getDefaultFilters() {
		return Stream.concat(defaultFilters.stream(),parentContext.getDefaultFilters().stream()).collect(Collectors.toList());
	}

	public void addExecution(Class<?> controllerClass, Method method, String baseUri, String classPath, ControllerMatch matchResult) {
		Set<HttpMethod> httpMethods = matchResult.getHttpMethods();
		IParameterHandler[] args = getParameterHandler().initializeHandlers(method, baseUri, classPath);
		for (HttpMethod httpMethod : httpMethods) {
			Request request = new Request(httpMethod, baseUri);
			ControllerFactory factory = getControllerFactory();
			ControllerExecution execution = new ControllerExecution(() -> factory.getInstance(controllerClass), method, args, getParameterHandler(), getExceptionRegistry(), matchResult, getFilters(method));
			if (getControllerFilter().accepts(request, execution)) {
				varServlet.executions.put(request, execution);
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
		List<Filter> filters = getDefaultFilters().stream().map(f -> getAndInitializeDefaultFilter(method, f)).collect(Collectors.toList());

		filters.addAll(filterAnnotations.stream().map(f -> getAndInitializeFilter(method, f)).collect(Collectors.toList()));

		return filters;
	}

	private Filter getAndInitializeFilter(Method method, FilterTuple filterTuple) {
		Filter filter = getFilterFactory().getInstance(filterTuple.getFilter().value());
		if (filter instanceof VarFilter) {
			((VarFilter) filter).init(method, filterTuple.getFilter(), filterTuple.getAnnotation());
		}
		return filter;
	}

	private Filter getAndInitializeDefaultFilter(Method method, Class<? extends Filter> filterTuple) {
		Filter filter = getFilterFactory().getInstance(filterTuple);
		if (filter instanceof VarFilter) {
			((VarFilter) filter).init(method, null, null);
		}
		return filter;
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

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}


	public void addDefaultFilter(Class<? extends Filter> filter) {
		defaultFilters.add(filter);
	}

	public void addControllerMatcher(ControllerMatcher controllerMatcher) {
		this.controllerMatchers.add(controllerMatcher);
	}

	public void addParameterHandler(Class<? extends IParameterHandlerMatcher> handlerMatcher) {
		parameterHandler.addParameterHandler(handlerMatcher);
	}

	public void setControllerFactory(ControllerFactory controllerFactory) {
		this.controllerFactory = controllerFactory;
	}
}
