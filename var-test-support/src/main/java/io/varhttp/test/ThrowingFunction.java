package io.varhttp.test;

@FunctionalInterface
public interface ThrowingFunction<I, O, E extends Exception> {

	O apply(I i) throws E;
}