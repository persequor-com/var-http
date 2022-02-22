package io.varhttp.parameterhandlers;

import io.varhttp.ControllerContext;
import io.varhttp.VarFilterChain;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public interface IParameterHandler {
	Object handle(ControllerContext controllerContext);
}
