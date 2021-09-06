package io.varhttp;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Set;

public class ControllerMapper {
	Logger logger = LoggerFactory.getLogger(ControllerMapper.class);

	private final AnnotationsHelper annotationsHelper;

	@Inject
	public ControllerMapper(AnnotationsHelper annotationsHelper) {
		this.annotationsHelper = annotationsHelper;
	}

	public void map(VarServlet varServlet, String basePackage, ControllerFactory controllerFactory) {
		Reflections reflections = new Reflections(basePackage);

		Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(ControllerClass.class);
		for (Class<?> controllerClass : typesAnnotatedWith) {
			map(varServlet, controllerClass, controllerFactory
			);
		}
	}

	public void map(VarServlet varServlet, Class<?> controllerClass, ControllerFactory controllerFactory) {
		Arrays.stream(controllerClass.getMethods())
				.filter(m -> m.getAnnotation(Controller.class) != null)
				.forEach(method -> {
					try {
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
						String urlMapKey = classPath + controllerPath;
						varServlet.addExecution(() -> controllerFactory.getInstance(controllerClass), method, urlMapKey, classPath);
					} catch (Exception e) {
						logger.error("Unable to register controller", e);
					}
				});
	}

}
