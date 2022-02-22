package io.varhttp;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PACKAGE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Filters.class)
public @interface Filter {
	Class<?> value();
}
