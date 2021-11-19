package io.varhttp;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Controller {
	String path();
	HttpMethod[] httpMethods() default {HttpMethod.GET};
	String contentType() default "";
	String summary() default "";
	String notes() default "";
	String[] consumes() default {};
}
