package io.varhttp;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
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
		typesHashMap.put(Boolean.class   , new Primitive(Boolean::valueOf, Boolean.FALSE));
		typesHashMap.put(Integer.class   , new Primitive(Integer::valueOf, Integer.valueOf(0)));
		typesHashMap.put(Long.class      , new Primitive(Long::valueOf, Long.valueOf(0)));
		typesHashMap.put(Double.class    , new Primitive(Double::valueOf, Double.valueOf(0)));
		typesHashMap.put(Float.class     , new Primitive(Float::valueOf, Float.valueOf(0)));
		typesHashMap.put(String.class    , new Primitive(String::valueOf, null));
		typesHashMap.put(BigDecimal.class      , new Primitive(BigDecimal::new, null));
		typesHashMap.put(BigInteger.class      , new Primitive(BigInteger::new, null));
		typesHashMap.put(Date.class            , new Primitive(z -> Date.from(ZonedDateTime.parse(z, dateTimeFormatter).toInstant()), null));
		typesHashMap.put(ZonedDateTime.class   , new Primitive(z -> ZonedDateTime.parse(z, dateTimeFormatter),null));
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
}
