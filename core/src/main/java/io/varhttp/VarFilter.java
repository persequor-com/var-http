package io.varhttp;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public interface VarFilter extends javax.servlet.Filter {
	void init(Method method, io.varhttp.Filter f, Annotation annotation);
}
