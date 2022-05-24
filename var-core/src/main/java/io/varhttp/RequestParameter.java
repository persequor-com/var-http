package io.varhttp;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface RequestParameter {
	String name() default "";
	boolean required() default false;
	String defaultValue() default "";
	String description() default "";
}