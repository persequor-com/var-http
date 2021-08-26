package io.varhttp;

import javax.inject.Inject;
import javax.inject.Provider;

public class TestStandalone extends Standalone {
	private ControllerFactory controllerFactory;
	private ExceptionRegistry exceptionRegistry;

	@Inject
	public TestStandalone(ControllerMapper controllerMapper, VarConfig varConfig,
						  ControllerFactory controllerFactory, ExceptionRegistry exceptionRegistry,
						  Provider<ParameterHandler> parameterHandlerProvider, FilterFactory filterFactory) {
		super(controllerMapper, varConfig, parameterHandlerProvider, filterFactory, new ControllerFilter(), "");
		this.controllerFactory = controllerFactory;
		this.exceptionRegistry = exceptionRegistry;
	}

	public void addController(String path, Class<?> clazz) {
		try {
			servlet.addExecution(() -> controllerFactory.getInstance(clazz), clazz.getMethod("c2"), path, exceptionRegistry, "");
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}
}
