package io.varhttp.parameterhandlers;

import io.varhttp.TypeHelper;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class Convert {
	public Object convert(String parameter, Class<?> type, String defaultValue) {
		if (parameter == null) {
			if (TypeHelper.isValidDefaultValue(defaultValue)) {
				return TypeHelper.parse(type, defaultValue);
			} else {
				return TypeHelper.defaultValue(type);
			}
		}
		if (type.isEnum()) {
			try {
				return type.getMethod("valueOf", String.class).invoke(null, parameter);
			} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
				throw new RuntimeException("valueOf method missing from enum class: "+type.getName()+". This should not be possible.");
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
