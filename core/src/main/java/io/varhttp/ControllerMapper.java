package io.varhttp;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

public class ControllerMapper {
	Logger logger = LoggerFactory.getLogger(ControllerMapper.class);

	private final AnnotationsHelper annotationsHelper;

	@Inject
	public ControllerMapper(AnnotationsHelper annotationsHelper) {
		this.annotationsHelper = annotationsHelper;
	}

	public void map(VarConfigurationContext context, String basePackage) {
		Reflections reflections = new Reflections(basePackage);

		Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(ControllerClass.class);
		for (Class<?> controllerClass : typesAnnotatedWith) {
			map(context, controllerClass);
		}
	}

	public void map(VarConfigurationContext context, Class<?> controllerClass) {
		Arrays.stream(controllerClass.getMethods())
				.forEach(method -> {
					try {
						AnnotationsHelper.Annotations annotations = annotationsHelper.getCumulativeAnnotations(method);
						Optional<ControllerMatch> matchResult = context.getControllerMatchers().stream().map(m -> m.find(method)).filter(Optional::isPresent).map(Optional::get).findFirst();
						if (!matchResult.isPresent()) {
							return;
						}
						String controllerPath = matchResult.get().getPath();
						if (!controllerPath.startsWith("/")) {
							throw new RuntimeException("Controller path for " + controllerPath +
									" should have a leading forward slash");
						}

						String basePrefix = context.getBasePath();
						String packagePrefix = annotations.get(ControllerPackage.class).map(ControllerPackage::pathPrefix).orElse("");
						String classPrefix = annotations.get(ControllerClass.class).map(ControllerClass::pathPrefix).orElse("");
						String classPath = basePrefix + packagePrefix + classPrefix;
						String urlMapKey = classPath + controllerPath;
						context.addExecution(controllerClass, method, urlMapKey, classPath, matchResult.get(), context);
					} catch (Exception e) {
						logger.error("Unable to register controller", e);
					}
				});
	}

}
