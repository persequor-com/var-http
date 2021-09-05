package io.varhttp.parameterhandlers;

import io.varhttp.PathVariable;
import io.varhttp.VarInitializationException;

import javax.inject.Inject;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathVariableParameterHandlerMatcher implements IParameterHandlerMatcher {
	private Convert convert;

	@Inject
	public PathVariableParameterHandlerMatcher(Convert convert) {
		this.convert = convert;
	}

	@Override
	public int getPriority() {
		return 70;
	}

	@Override
	public IParameterHandler getHandlerIfMatches(Method method, Parameter parameter, String path, String classPath) {
		PathVariable pathVariableAnnotation = parameter.getAnnotation(PathVariable.class);
		if (pathVariableAnnotation != null) {
			String name = pathVariableAnnotation.name();
			if("".equals(name)) {
				throw new RuntimeException("Could not determine @PathVariable name annotation for controller: " + method.getName());
			}

			Pattern pattern = Pattern.compile(path.replaceAll("\\{"+name+"\\}","([^\\/\\?]+)").replaceAll("\\{\\w+\\}","[^\\/\\?]+"));
			Matcher matcher = Pattern.compile("\\{"+name+"\\}").matcher(path);
			if (!matcher.find()) {
				throw new VarInitializationException("Could not find path variable "+name+" in controller path: "+path);
			}
			return new PathVariableParameterHandler(new MatchContext(method, parameter, parameter.getType()), pattern, convert);
		}

		return null;
	}
}
