package io.varhttp.parameterhandlers;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class MatchContext {
	private Method method;
	private Parameter parameter;
	private Class<?> type;
	private String defaultValue;

	public MatchContext(Method method, Parameter parameter, Class<?> type) {
		this.method = method;
		this.parameter = parameter;
		this.type = type;
	}

	public Parameter getParameter() {
		return parameter;
	}

	public void setParameter(Parameter parameter) {
		this.parameter = parameter;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public MatchContext setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
		return this;
	}
}
