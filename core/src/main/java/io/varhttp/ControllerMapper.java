package io.varhttp;

import org.reflections.Reflections;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.Set;

public class ControllerMapper {

	private final ControllerFactory injector;
	private final ExceptionRegistry exceptionRegistry;
	private final AnnotationsHelper annotationsHelper;

	@Inject
	public ControllerMapper(ControllerFactory injector,
							ExceptionRegistry exceptionRegistry
							, AnnotationsHelper annotationsHelper) {
		this.injector = injector;
		this.exceptionRegistry = exceptionRegistry;
		this.annotationsHelper = annotationsHelper;
	}

	public void map(VarServlet varServlet, String basePackage) {
		Reflections reflections = new Reflections(basePackage);

		Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(ControllerClass.class);
		for (Class<?> controllerClass : typesAnnotatedWith) {
			map(varServlet, controllerClass);
		}
	}

	public void map(VarServlet varServlet, Class<?> controllerClass) {
		Arrays.stream(controllerClass.getMethods())
				.filter(m -> m.getAnnotation(Controller.class) != null)
				.forEach(method -> {
					Controller controllerAnnotation = method.getAnnotation(Controller.class);
					AnnotationsHelper.Annotations annotations = annotationsHelper.getCumulativeAnnotations(method);

					String controllerPath = controllerAnnotation.path();
					if (!controllerPath.startsWith("/")) {
						throw new RuntimeException("Controller path for " + controllerPath +
								" should have a leading forward slash");
					}

					String basePrefix = varServlet.getBasePath();
					String packagePrefix = annotations.get(ControllerPackage.class).map(ControllerPackage::pathPrefix).orElse("");
					String classPrefix = annotations.get(ControllerClass.class).map(ControllerClass::pathPrefix).orElse("");
					String classPath = basePrefix + packagePrefix + classPrefix;
					String urlMapKey =  classPath + controllerPath;
					varServlet.addExecution(() -> injector.getInstance(controllerClass), method, urlMapKey, exceptionRegistry, classPath);
				});
	}

}
