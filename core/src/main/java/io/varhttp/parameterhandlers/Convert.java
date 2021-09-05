package io.varhttp.parameterhandlers;

import io.varhttp.TypeHelper;

import java.util.Optional;

public class Convert {
	public Object convert(String parameter, Class<?> type, String defaultValue) {
		if (parameter == null) {
			if (defaultValue != null && !"".equals(defaultValue)) {
				return TypeHelper.parse(type, defaultValue);
			} else {
				return TypeHelper.defaultValue(type);
			}
		}
		if (TypeHelper.isStandardType(type)) {
			return TypeHelper.parse(type, parameter);
		}
		if (type.equals(Optional.class)) {
			return Optional.ofNullable(parameter);
		}
		throw new RuntimeException("Unhandled conversion type: "+type.getName());
	}
}
