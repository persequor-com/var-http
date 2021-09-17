package io.varhttp;

import io.varhttp.parameterhandlers.IParameterHandler;
import io.varhttp.parameterhandlers.IParameterHandlerMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ParameterHandlerTest {
	@InjectMocks
	ParameterHandler parameterHandler;
	@Mock
	Serializer serializer;
	@Mock
	ParameterHandlerMatcherFactory handlerMatcherFactory;
	private Handler1 handler1 = spy(new Handler1());
	private Handler2 handler2 = spy(new Handler2());

	@Before
	public void setup() {
		when(handlerMatcherFactory.get(Handler1.class)).thenReturn(handler1);
		when(handlerMatcherFactory.get(Handler2.class)).thenReturn(handler2);
	}

	@Test
	public void sortingRegression_samePriority_sortsByArbitrarily() throws NoSuchMethodException {
		when(handler1.getPriority()).thenReturn(5);
		when(handler2.getPriority()).thenReturn(5);

		parameterHandler.addParameterHandler(Handler1.class);
		parameterHandler.addParameterHandler(Handler2.class);

		parameterHandler.initializeHandlers(ParameterHandlerTest.class.getMethod("methodWithParameters", String.class), "base", "path for class");

		InOrder inOrder = Mockito.inOrder(handler1, handler2);

		inOrder.verify(handler1).getHandlerIfMatches(any(Method.class), any(Parameter.class), anyString(), anyString());
		inOrder.verify(handler2).getHandlerIfMatches(any(Method.class), any(Parameter.class), anyString(), anyString());
	}

	@Test
	public void sortingRegression_sameDifferentPriority_sortsByPriority() throws NoSuchMethodException {
		when(handler1.getPriority()).thenReturn(7);
		when(handler2.getPriority()).thenReturn(5);

		parameterHandler.addParameterHandler(Handler1.class);
		parameterHandler.addParameterHandler(Handler2.class);

		parameterHandler.initializeHandlers(ParameterHandlerTest.class.getMethod("methodWithParameters", String.class), "base", "path for class");

		InOrder inOrder = Mockito.inOrder(handler1, handler2);

		inOrder.verify(handler2).getHandlerIfMatches(any(Method.class), any(Parameter.class), anyString(), anyString());
		inOrder.verify(handler1).getHandlerIfMatches(any(Method.class), any(Parameter.class), anyString(), anyString());
	}

	public void methodWithParameters(String string) {

	}

	private static class Handler1 implements IParameterHandlerMatcher {
		@Override
		public int getPriority() {
			return 5;
		}

		@Override
		public IParameterHandler getHandlerIfMatches(Method method, Parameter parameter, String path, String classPath) {
			return null;
		}
	}

	private static class Handler2 implements IParameterHandlerMatcher {
		@Override
		public int getPriority() {
			return 5;
		}

		@Override
		public IParameterHandler getHandlerIfMatches(Method method, Parameter parameter, String path, String classPath) {
			return null;
		}
	}
}