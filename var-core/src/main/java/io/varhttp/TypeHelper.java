package io.varhttp;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Function;

public class TypeHelper {
	private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;
	private static HashMap<Class<?>, Primitive> typesHashMap = new HashMap<>();
	static {
		typesHashMap.put(boolean.class   , new Primitive(Boolean::parseBoolean, false));
		typesHashMap.put(int.class       , new Primitive(Integer::parseInt, 0));
		typesHashMap.put(long.class      , new Primitive(Long::parseLong, 0));
		typesHashMap.put(double.class    , new Primitive(Double::parseDouble, 0));
		typesHashMap.put(float.class     , new Primitive(Float::parseFloat, 0));
		typesHashMap.put(Boolean.class   , new Primitive(Boolean::valueOf, null));
		typesHashMap.put(Integer.class   , new Primitive(Integer::valueOf, null));
		typesHashMap.put(Long.class      , new Primitive(Long::valueOf, null));
		typesHashMap.put(Double.class    , new Primitive(Double::valueOf, null));
		typesHashMap.put(Float.class     , new Primitive(Float::valueOf, null));
		typesHashMap.put(String.class    , new Primitive(String::valueOf, null));
		typesHashMap.put(BigDecimal.class      , new Primitive(BigDecimal::new, null));
		typesHashMap.put(BigInteger.class      , new Primitive(BigInteger::new, null));
		typesHashMap.put(Date.class            , new Primitive(z -> Date.from(ZonedDateTime.parse(z, dateTimeFormatter).toInstant()), null));
		typesHashMap.put(ZonedDateTime.class   , new Primitive(z -> ZonedDateTime.parse(z, dateTimeFormatter),null));
		typesHashMap.put(LocalDate.class   , new Primitive(LocalDate::parse,null));
		typesHashMap.put(UUID.class        , new Primitive(UUID::fromString,null));

	}

	public static Object defaultValue(Class<?> type) {
		if (isStandardType(type)) {
			return typesHashMap.get(type).defaultValue;
		} else {
			return null;
		}
	}

	public static Object parse(Class<?> type, String stringValue) {
		if (isStandardType(type)) {
			return typesHashMap.get(type).converter.apply(stringValue);
		} else {
			return stringValue;
		}

	}

	public static boolean isStandardType(Class<?> type) {
		return typesHashMap.containsKey(type);
	}

	private static class Primitive {
		Function<String,?> converter;
		Object defaultValue;

		public Primitive(Function<String,?> converter, Object defaultValue) {
			this.converter = converter;
			this.defaultValue = defaultValue;
		}
	}

	/**
	 * Checks if a given string is a valid default value for a parameter.
	 * @param defaultValue The string to check
	 * @return true if the string is a valid default value, false otherwise
	 */
	public static boolean isValidDefaultValue(String defaultValue) {
		return defaultValue != null && !"".equals(defaultValue);
	}
}
