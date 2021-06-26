package io.varhttp;

public interface FilterFactory {
	javax.servlet.Filter getInstance(Class<? extends javax.servlet.Filter> filterClass);
}
