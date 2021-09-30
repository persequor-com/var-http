package io.varhttp;

import io.varhttp.parameterhandlers.IParameterHandlerMatcher;

import javax.servlet.Filter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class VarConfiguration {
	private VarServlet servlet = null;
	private final VarConfigurationContext context;
	private ControllerMapper controllerMapper = null;
	private final ParameterHandler parameterHandler;
	private ExceptionRegistry exceptionRegistry;
	List<Runnable> mappings = new ArrayList<>();


	public VarConfiguration(VarServlet servlet, ControllerMapper controllerMapper, VarConfigurationContext context, ParameterHandler parameterHandler) {
		this.servlet = servlet;
		this.controllerMapper = controllerMapper;
		this.context = context;
		this.parameterHandler = parameterHandler;
	}

	public void addControllerPackage(Package controllerPackage) {
		mappings.add(() -> {
			controllerMapper.map(context, controllerPackage.getName());
		});
	}

	public void addController(Class<?> controller) {
		mappings.add(() -> {
			controllerMapper.map(context, controller);
		});
	}

	public void setObjectFactory(ObjectFactory objectFactory) {
		this.context.setObjectFactory(objectFactory);
	}

	public void addParameterHandler(Class<? extends IParameterHandlerMatcher> handlerMatcher) {
		context.addParameterHandler(handlerMatcher);
	}

	public void addDefaultFilter(Class<? extends Filter> filter) {
		context.addDefaultFilter(filter);
	}

	public void addDefaultVarFilter(Class<?> filterClass) {
		context.addDefaultVarFilter(filterClass);
	}

	public void addDefaultVarFilter(Class<?> filterClass, Method method) {
		context.addDefaultVarFilter(filterClass, method);
	}

	public void setNotFoundController(Class<?> filterClass) {
		context.setNotFoundController(filterClass);
	}

	public void setNotFoundController(Class<?> filterClass, Method method) {
		context.setNotFoundController(filterClass, method);
	}

	public void addControllerMatcher(ControllerMatcher controllerMatcher) {
		context.addControllerMatcher(controllerMatcher);
	}

	public void addExceptionMapper(ControllerExceptionMapper controllerExceptionMapper) {
		context.getExceptionRegistry().registerException(controllerExceptionMapper);
	}

	public void setBasePath(String basePath) {
		context.setBasePath(basePath);
		servlet.executions.createPathContext(context, basePath);
	}

	public void configure(Consumer<VarConfiguration> configurationConsumer) {
		VarConfigurationContext newContext = new VarConfigurationContext(servlet, context, parameterHandler);
		VarConfiguration configuration = new VarConfiguration(servlet, controllerMapper, newContext, parameterHandler);
		configurationConsumer.accept(configuration);
		newContext.applyMappings();
		configuration.applyMappings();
	}

	public void applyMappings() {
		mappings.forEach(Runnable::run);
	}

	public void onControllerAdd(Consumer<Method> methodConsumer) {
		context.onControllerAdd(methodConsumer);
	}
}
