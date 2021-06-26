package io.varhttp;

import io.varhttp.performance.Class1;

import javax.inject.Inject;

public class TestStandalone extends Standalone {
	private ControllerFactory controllerFactory;
	private ExceptionRegistry exceptionRegistry;

	@Inject
	public TestStandalone(VarServlet servlet, ControllerMapper controllerMapper, VarConfig varConfig, ControllerFactory controllerFactory, ExceptionRegistry exceptionRegistry) {
		super(servlet, controllerMapper, varConfig);
		this.controllerFactory = controllerFactory;
		this.exceptionRegistry = exceptionRegistry;
	}

	public void addController(String path, Class<?> clazz) {
		try {
			servlet.addExecution(() -> controllerFactory.getInstance(clazz), clazz.getMethod("c2"), path, exceptionRegistry);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}
}
