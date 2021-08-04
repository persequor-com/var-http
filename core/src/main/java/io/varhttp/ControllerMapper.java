package io.varhttp;

import org.reflections.Reflections;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Arrays;
import java.util.Set;

public class ControllerMapper {

	private final ControllerFactory injector;
	private final ExceptionRegistry exceptionRegistry;

	@Inject
	public ControllerMapper(ControllerFactory injector,
							ExceptionRegistry exceptionRegistry) {
		this.injector = injector;
		this.exceptionRegistry = exceptionRegistry;
	}

	public void map(VarServlet varServlet, String basePackage) {

		Reflections reflections = new Reflections(basePackage);

		Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(ControllerClass.class);
		for (Class<?> controllerClass : typesAnnotatedWith) {

			Arrays.stream(controllerClass.getMethods())
					.filter(m -> m.getAnnotation(Controller.class) != null)
					.forEach(method -> {
						Controller controllerAnnotation = method.getAnnotation(Controller.class);

						String controllerPath = controllerAnnotation.path();
						if (!controllerPath.startsWith("/")) {
							throw new RuntimeException("Controller path for " + controllerPath +
									" should have a leading forward slash");
						}

						String baseUri = varServlet.getBasePath();
						String urlMapKey = baseUri + controllerPath;
						varServlet.addExecution(() -> injector.getInstance(controllerClass), method, urlMapKey, exceptionRegistry);
					});
		}
	}

}
