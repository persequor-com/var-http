package io.varhttp;

import org.reflections.Reflections;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Arrays;
import java.util.Set;

public class ControllerMapper {

	private final ControllerFactory injector;
	private final ExceptionRegistry exceptionRegistry;
	private final VarServlet varServlet;

	@Inject
	public ControllerMapper(ControllerFactory injector,
							ExceptionRegistry exceptionRegistry,
							VarServlet varServlet) {
		this.injector = injector;
		this.exceptionRegistry = exceptionRegistry;
		this.varServlet = varServlet;
	}

	public void map(String basePackage) {

		Reflections reflections = new Reflections(basePackage);

		Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(ControllerClass.class);
		for (Class<?> controllerClass : typesAnnotatedWith) {

			Arrays.stream(controllerClass.getMethods())
					.filter(m -> m.getAnnotation(Controller.class) != null)
					.forEach(method -> {
						Controller controllerAnnotation = method.getAnnotation(Controller.class);

						String servicepath = "";
						if (!controllerAnnotation.path().startsWith("/")) {
							throw new RuntimeException("Controller path for " + controllerAnnotation.path() +
									" should have a leading forward slash");
						}

						String baseUri = servicepath;
						String urlMapKey = baseUri + controllerAnnotation.path();
						varServlet.addExecution(() -> injector.getInstance(controllerClass), method, urlMapKey, exceptionRegistry);

					});
		}
	}

}
