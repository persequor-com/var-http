package io.varhttp.controllers;

import io.varhttp.Filter;
import io.varhttp.controllers.withfilters.AuthenticationFilter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Filter(AuthenticationFilter.class)
public @interface AltControllerAnnotation {
	String urlPath();
}
