package io.varhttp.parameterhandlers;

import io.varhttp.TypeHelper;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class Convert {
	public Object convert(String parameter, Class<?> requestedType, String defaultValue) {
		if (parameter == null || (!String.class.equals(requestedType) && "".equals(parameter))) {
			if (TypeHelper.isValidDefaultValue(defaultValue)) {
				return TypeHelper.parse(requestedType, defaultValue);
			} else {
				return TypeHelper.defaultValue(requestedType);
			}
		}
		if (requestedType.isEnum()) {
			try {
				return requestedType.getMethod("valueOf", String.class).invoke(null, parameter);
			} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
				throw new RuntimeException("valueOf method missing from enum class: "+requestedType.getName()+". This should not be possible.");
			}
		}
		if (TypeHelper.isStandardType(requestedType)) {
			return TypeHelper.parse(requestedType, parameter);
		}
		if (requestedType.equals(Optional.class)) {
			return Optional.ofNullable(parameter);
		}
		throw new RuntimeException("Unhandled conversion requestedType: "+requestedType.getName());
	}
}
