package io.varhttp.parameterhandlers;

import io.varhttp.ControllerContext;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RequestParameterListHandler implements IParameterHandler {
	private final RequestParameterHandler.Configuration configuration;
	private final Type listElementType;
	private Convert convert;

	public RequestParameterListHandler(RequestParameterHandler.Configuration configuration, Type listElementType, Convert convert) {
		this.configuration = configuration;
		this.listElementType = listElementType;
		this.convert = convert;
	}

	@Override
	public Object handle(ControllerContext controllerContext) {
		List<String> parameterValues = controllerContext.getParameters().getAll(configuration.getName());
		if (parameterValues != null) {
			return parameterValues.stream().map(p -> convert.convert(p, (Class<?>) listElementType, configuration.getDefaultValue())).collect(Collectors.toList());
		} else {
			return null;
		}
	}
}
