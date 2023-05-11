package io.varhttp.parameterhandlers;

import io.varhttp.ControllerContext;
import io.varhttp.TypeHelper;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class RequestParameterHandler implements IParameterHandler {
	private Configuration configuration;
	private Convert convert;

	public RequestParameterHandler(Configuration configuration, Convert convert) {
		this.configuration = configuration;
		this.convert = convert;
	}

	@Override
	public Object handle(ControllerContext controllerContext) {
		String parameter = controllerContext.getParameters().get(configuration.getName());
		if(parameter == null && configuration.isRequired()) {
			throw new MissingParamException("Required parameter " + configuration.getName() + " not found");
		}
		return convert.convert(parameter, configuration.getType(), configuration.getDefaultValue());
	}

	public static class Configuration extends MatchContext {
		private final String name;
		boolean required = false;

		public Configuration(Method method, Parameter parameter, Class<?> type, String name, boolean required, String defaultValue) {
			super(method, parameter, type);
			this.name = name;
			if(TypeHelper.isValidDefaultValue(defaultValue)) {
				this.required = false;
			} else {
				this.required = required;
			}
			this.setDefaultValue(defaultValue);
		}
		public boolean isRequired() {
			return required;
		}

		public void setRequired(boolean required) {
			this.required = required;
		}

		public String getName() {
			return name;
		}
	}
}
