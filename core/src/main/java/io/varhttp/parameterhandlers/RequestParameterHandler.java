package io.varhttp.parameterhandlers;

import io.varhttp.ControllerContext;

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
		return convert.convert(controllerContext.request().getParameter(configuration.getName()), configuration.getType(), configuration.getDefaultValue());
	}

	public static class Configuration extends MatchContext {
		private final String name;
		boolean required = false;

		public Configuration(Method method, Parameter parameter, Class<?> type, String name, boolean required, String defaultValue) {
			super(method, parameter, type);
			this.name = name;
			this.required = required;
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
