package io.varhttp;

import io.varhttp.parameterhandlers.IParameterHandler;
import io.varhttp.parameterhandlers.IParameterHandlerMatcher;

import javax.servlet.Filter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
	List<Object> defaultFilters = new ArrayList<>();
	ControllerExecution notFoundController;

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


	List<Object> getDefaultFilters() {
		return Stream.concat(defaultFilters.stream(),parentContext.getDefaultFilters().stream()).collect(Collectors.toList());
	}

	public void addExecution(Class<?> controllerClass, Method method, String baseUri, String classPath, ControllerMatch matchResult, VarConfigurationContext context) {
		Set<HttpMethod> httpMethods = matchResult.getHttpMethods();
		IParameterHandler[] args = getParameterHandler().initializeHandlers(method, baseUri, classPath);
		for (HttpMethod httpMethod : httpMethods) {
			Request request = new Request(httpMethod, baseUri);
			ControllerFactory factory = getControllerFactory();
			ControllerExecution execution = new ControllerExecution(() -> factory.getInstance(controllerClass), method, args, getParameterHandler(), getExceptionRegistry(), matchResult, getFilters(method));
			if (getControllerFilter().accepts(request, execution)) {
				varServlet.executions.put(context, request, execution);
			}
		}
	}

	private List<Object> getFilters(Method method) {
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

	private List<Object> getFilters(Method method, Set<FilterTuple> filterAnnotations) {
		List<Object> filters = new ArrayList<>(getDefaultFilters());

		filters.addAll(filterAnnotations.stream().map(f -> {
			Object filter = getFilterFactory().getInstance(f.getFilter().value());
			if (filter instanceof VarFilter) {
				((VarFilter) filter).init(method, f.getFilter(), f.getAnnotation());
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

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}


	public void addDefaultFilter(Class<? extends Filter> filter) {
		defaultFilters.add(getFilterFactory().getInstance(filter));
	}

	public void addDefaultVarFilter(Class<?> controllerClass) {
		final Optional<Method> methodAnnotated = getMethodByAnnotation(controllerClass, FilterMethod.class);

		addDefaultVarFilter(controllerClass, methodAnnotated.get());
	}

	public void addDefaultVarFilter(Class<?> filterClass, Method method) {
		ControllerFactory factory = getControllerFactory();
		IParameterHandler[] args = getParameterHandler().initializeHandlers(method, null, null);

		VarFilterExecution filterExecution = new VarFilterExecution(() -> factory.getInstance(filterClass), method, args, parameterHandler, new ControllerMatch(method, null, null, ""));
		defaultFilters.add(filterExecution);
	}

	public void setNotFoundController(Class<?> controllerClass) {
		final Optional<Method> methodAnnotated = getMethodByAnnotation(controllerClass, NotFoundController.class);

		setNotFoundController(controllerClass, methodAnnotated.get());
	}

	private Optional<Method> getMethodByAnnotation(Class<?> controllerClass, Class annotationClass) {
		final Optional<Method> methodAnnotated = Arrays.stream(controllerClass.getMethods())
				.filter(method -> method.isAnnotationPresent(annotationClass))
				.findFirst();

		if(!methodAnnotated.isPresent()) {
			throw new RuntimeException("No method annotated with" + annotationClass.getName());
		}
		return methodAnnotated;
	}

	public void setNotFoundController(Class<?> controllerClass, Method method) {
		ControllerFactory factory = getControllerFactory();
		IParameterHandler[] args = getParameterHandler().initializeHandlers(method, null, null);

		ControllerMatch matchResult = new ControllerMatch(method, "", new HashSet<>(), "");
		notFoundController = new ControllerExecution(() -> factory.getInstance(controllerClass), method, args, getParameterHandler(), getExceptionRegistry(), matchResult, getFilters(method));
	}

	public ControllerExecution getNotFoundController() {
		if(notFoundController == null) {
			return parentContext.getNotFoundController();
		}

		return notFoundController;
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
