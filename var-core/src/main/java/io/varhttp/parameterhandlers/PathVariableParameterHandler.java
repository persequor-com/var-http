package io.varhttp.parameterhandlers;

import io.varhttp.ControllerContext;
import io.varhttp.HttpHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathVariableParameterHandler implements IParameterHandler {
	private MatchContext matchContext;
	private Pattern pattern;
	private Convert convert;

	public PathVariableParameterHandler(MatchContext matchContext,Pattern pattern, Convert convert) {
		this.matchContext = matchContext;
		this.pattern = pattern;
		this.convert = convert;
	}

	@Override
	public Object handle(ControllerContext controllerContext) {
		Matcher matcher = pattern.matcher(HttpHelper.decode(controllerContext.request().getRequestURI(), controllerContext.request()));

		if(matcher.find()) {
			return convert.convert(matcher.group(1), matchContext.getType(), matchContext.getDefaultValue());
		} else {
			return null;
		}
	}
}
