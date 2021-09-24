package io.varhttp;

import io.varhttp.parameterhandlers.IParameterHandler;
import io.varhttp.parameterhandlers.IParameterHandlerMatcher;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
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
	List<Object> defaultFilters = new ArrayList<>();
	ControllerExecution notFoundController;
	List<Runnable> mappings = new ArrayList<>();

	public VarConfigurationContext(VarServlet varServlet, VarConfigurationContext parentContext,
								   ParameterHandler parameterHandler) {
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
			Class<?> filterClass = f.getFilter().value();
			if (Filter.class.isAssignableFrom(filterClass)) {

				Object filter = getFilterFactory().getInstance(filterClass);
				if (filter instanceof VarFilter) {
					((VarFilter) filter).init(method, f.getFilter(), f.getAnnotation());
				}
				return filter;
			} else {
				return getVarFilterExecution(filterClass).ifVarFilter((VarFilter filter) -> {filter.init(method, f.getFilter(), f.getAnnotation());});
			}
		}).collect(Collectors.toList()));

		return filters;
	}

	private Object getAndInitializeFilter(Method method, FilterTuple filterTuple) {
		Object filter = getFilterFactory().getInstance(filterTuple.getFilter().value());
		if (filter instanceof VarFilter) {
			((VarFilter) filter).init(method, filterTuple.getFilter(), filterTuple.getAnnotation());
		}
		return filter;
	}

	private Object getAndInitializeDefaultFilter(Method method, Class<?> filterTuple) {
		Object filter = getFilterFactory().getInstance(filterTuple);
		if (filter instanceof VarFilter) {
			((VarFilter) filter).init(method, null, null);
		}
		return filter;
	}

	private Set<FilterTuple> getFilterAnnotations(Annotation[] annotations) {
		return Arrays.stream(annotations).flatMap(annotation -> {
			if (annotation instanceof Filters) {
				return Arrays.stream(((Filters) annotation).value()).map(filter -> {
					return new FilterTuple(filter, filter);
				});
			} else if (annotation instanceof io.varhttp.Filter) {
				return Stream.of(new FilterTuple((io.varhttp.Filter) annotation, annotation));
			} else if (annotation.annotationType().getAnnotation(io.varhttp.Filter.class) != null) {
				return Stream.of(new FilterTuple(annotation.annotationType().getAnnotation(io.varhttp.Filter.class), annotation));
			} else {
				return Stream.empty();
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

	public void addDefaultVarFilter(Class<?> filterClass) {
		defaultFilters.add(getVarFilterExecution(filterClass));
	}

	public VarFilterExecution getVarFilterExecution(Class<?> filterClass) {
		final Optional<Method> methodAnnotated = getMethodByAnnotation(filterClass, FilterMethod.class);
		return getVarFilterExecution(filterClass, methodAnnotated.orElseThrow(() -> new VarInitializationException("Could not find method annotated with @FilterMethod in class: "+filterClass.getName())));
	}

	public VarFilterExecution getVarFilterExecution(Class<?> filterClass, Method method) {
		ControllerFactory factory = getControllerFactory();
		IParameterHandler[] args = getParameterHandler().initializeHandlers(method, null, null);

		return new VarFilterExecution(() -> factory.getInstance(filterClass), method, args, parameterHandler, new ControllerMatch(method, null, null, ""));
	}

	public void addDefaultVarFilter(Class<?> filterClass, Method method) {
		defaultFilters.add(getVarFilterExecution(filterClass, method));
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
		mappings.add(() -> {
			ControllerFactory factory = getControllerFactory();
			IParameterHandler[] args = getParameterHandler().initializeHandlers(method, null, null);

			ControllerMatch matchResult = new ControllerMatch(method, "", new HashSet<>(), "");
			notFoundController = new ControllerExecution(() -> factory.getInstance(controllerClass), method, args, getParameterHandler(), getExceptionRegistry(), matchResult, getFilters(method));
		});
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


	public void applyMappings() {
		mappings.forEach(Runnable::run);
	}
}
