package io.varhttp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ControllerExecutionMapTest {

	ControllerExecutionMap controllerExecutionMap;
	@Mock
	private ControllerExecution execution1;
	@Mock
	private ControllerExecution execution2;
	@Mock
	private ControllerExecution execution3;

	private VarConfigurationContext context;
	private VarConfigurationContext otherContext;

	@Mock
	private VarConfigurationContext parentContext;

	@Mock
	private ControllerExecution controllerExe;

	@Mock
	private ControllerExecution parentContextExe;

	@Mock
	private VarServlet servlet;
	@Mock
	private ParameterHandler parameterHandler;

	@Before
	public void setup() {
		when(parentContext.getNotFoundController()).thenReturn(parentContextExe);
		controllerExecutionMap = new ControllerExecutionMap(parentContext);
		context = spy(new VarConfigurationContext(servlet, parentContext, parameterHandler));
		otherContext = spy(new VarConfigurationContext(servlet, parentContext, parameterHandler));
	}

	@Test
	public void happy() {
		controllerExecutionMap.put(context, new Request(HttpMethod.GET, "/my/path"), execution1);
		controllerExecutionMap.put(context, new Request(HttpMethod.GET, "/my/path2"), execution2);

		ControllerExecution actual = controllerExecutionMap.get("my/path".split("/"), HttpMethod.GET);
		assertSame(execution1, actual);

		actual = controllerExecutionMap.get("my/path2".split("/"), HttpMethod.GET);
		assertSame(execution2, actual);
	}

	@Test
	public void notADuplicate_ifDifferentHttpMethods() {
		controllerExecutionMap.put(context, new Request(HttpMethod.PUT, "/my/path"), execution1);
		controllerExecutionMap.put(context, new Request(HttpMethod.GET, "/my/path"), execution2);
	}


	@Test
	public void notADuplicate_ifDifferentHttpMethods_forBaseUrl() {
		controllerExecutionMap.put(context, new Request(HttpMethod.PUT, "/"), execution1);
		controllerExecutionMap.put(context, new Request(HttpMethod.GET, "/"), execution2);

		ControllerExecution actual = controllerExecutionMap.get(new String[]{}, HttpMethod.GET);
		assertSame(execution2, actual);
	}

	@Test
	public void passingEmptyStringPath() {
		controllerExecutionMap.put(context, new Request(HttpMethod.GET, "/"), execution2);

		ControllerExecution actual = controllerExecutionMap.get(new String[]{""}, HttpMethod.GET);
		assertSame(execution2, actual);
	}

	@Test(expected = RuntimeException.class)
	public void duplicate() {
		controllerExecutionMap.put(context, new Request(HttpMethod.GET, "/my/path"), execution1);
		controllerExecutionMap.put(context, new Request(HttpMethod.GET, "/my/path"), execution2);
	}

	@Test(expected = RuntimeException.class)
	public void duplicate_forBaseUrl() {
		controllerExecutionMap.put(context, new Request(HttpMethod.GET, "/"), execution1);
		controllerExecutionMap.put(context, new Request(HttpMethod.GET, "/"), execution2);
	}

	@Test
	public void simpleWildcard() {
		controllerExecutionMap.put(context, new Request(HttpMethod.GET, "/my/path/{wild1}"), execution1);
		controllerExecutionMap.put(context, new Request(HttpMethod.GET, "/my/path/{wild1}/sub"), execution2);

		ControllerExecution actual = controllerExecutionMap.get("my/path/muh".split("/"), HttpMethod.GET);
		assertSame(execution1, actual);

		actual = controllerExecutionMap.get("my/path/muh2/sub".split("/"), HttpMethod.GET);
		assertSame(execution2, actual);
	}

	@Test
	public void wildcardAndEmptyOnSameHttpMethod() {
		controllerExecutionMap.put(context, new Request(HttpMethod.GET, "/my/path"), execution2);
		controllerExecutionMap.put(context, new Request(HttpMethod.GET, "/my/path/{wild1}"), execution1);

		ControllerExecution actual = controllerExecutionMap.get("my/path".split("/"), HttpMethod.GET);
		assertSame(execution2, actual);

		actual = controllerExecutionMap.get("my/path/blah".split("/"), HttpMethod.GET);
		assertSame(execution1, actual);
	}

	@Test
	public void wildcardAndEmptyOnSameHttpMethod_reversed() {
		controllerExecutionMap.put(context, new Request(HttpMethod.GET, "/my/path/{wild1}"), execution1);
		controllerExecutionMap.put(context, new Request(HttpMethod.GET, "/my/path"), execution2);

		ControllerExecution actual = controllerExecutionMap.get("my/path".split("/"), HttpMethod.GET);
		assertSame(execution2, actual);

		actual = controllerExecutionMap.get("my/path/blah".split("/"), HttpMethod.GET);
		assertSame(execution1, actual);
	}

	@Test
	public void deepWildcard_clashesWithNonWildcard() {
		controllerExecutionMap.put(context, new Request(HttpMethod.GET, "/my/path/{wild1}/{wild2}"), execution1);
		controllerExecutionMap.put(context, new Request(HttpMethod.GET, "/my/path/{wild1}/sub"), execution2);

		ControllerExecution actual = controllerExecutionMap.get("my/path/something/something".split("/"), HttpMethod.GET);
		assertSame(execution1, actual);

		actual = controllerExecutionMap.get("my/path/something/sub".split("/"), HttpMethod.GET);
		assertSame(execution2, actual);
	}

	@Test
	public void deepWildcard_clashesWithNonWildcard_reverseOrder() {
		controllerExecutionMap.put(context, new Request(HttpMethod.GET, "/my/path/{wild1}/sub"), execution2);
		controllerExecutionMap.put(context, new Request(HttpMethod.GET, "/my/path/{wild1}/{wild2}"), execution1);

		ControllerExecution actual = controllerExecutionMap.get("my/path/something/something".split("/"), HttpMethod.GET);
		assertSame(execution1, actual);

		actual = controllerExecutionMap.get("my/path/something/sub".split("/"), HttpMethod.GET);
		assertSame(execution2, actual);
	}

	@Test
	public void multilevelWildcard() {
		controllerExecutionMap.put(context, new Request(HttpMethod.GET, "/*"), execution1);
		controllerExecutionMap.put(context, new Request(HttpMethod.GET, "/my/path"), execution2);
		controllerExecutionMap.put(context, new Request(HttpMethod.GET, "/other/path"), execution3);

		ControllerExecution actual = controllerExecutionMap.get("my/path".split("/"), HttpMethod.GET);
		assertSame(execution2, actual);

		actual = controllerExecutionMap.get("other/path".split("/"), HttpMethod.GET);
		assertSame(execution3, actual);

		actual = controllerExecutionMap.get("something/else/entirely".split("/"), HttpMethod.GET);
		assertSame(execution1, actual);

		actual = controllerExecutionMap.get("and/yet/another/different/path".split("/"), HttpMethod.GET);
		assertSame(execution1, actual);

		actual = controllerExecutionMap.get("".split("/"), HttpMethod.GET);
		assertSame(execution1, actual);
	}

	@Test
	public void multilevelWildcard_reversed() {
		controllerExecutionMap.put(context, new Request(HttpMethod.GET, "/other/path"), execution3);
		controllerExecutionMap.put(context, new Request(HttpMethod.GET, "/my/path"), execution2);
		controllerExecutionMap.put(context, new Request(HttpMethod.GET, "/*"), execution1);

		ControllerExecution actual = controllerExecutionMap.get("my/path".split("/"), HttpMethod.GET);
		assertSame(execution2, actual);

		actual = controllerExecutionMap.get("other/path".split("/"), HttpMethod.GET);
		assertSame(execution3, actual);

		actual = controllerExecutionMap.get("something/else/entirely".split("/"), HttpMethod.GET);
		assertSame(execution1, actual);

		actual = controllerExecutionMap.get("and/yet/another/different/path".split("/"), HttpMethod.GET);
		assertSame(execution1, actual);

		actual = controllerExecutionMap.get("".split("/"), HttpMethod.GET);
		assertSame(execution1, actual);
	}

	@Test
	public void notFoundController_happyPath() {
		controllerExecutionMap.put(context, new Request(HttpMethod.GET, "/other/path"), execution3);

		when(context.getNotFoundController()).thenReturn(controllerExe);
		when(parentContext.getNotFoundController()).thenReturn(parentContextExe);

		ControllerExecution actual = controllerExecutionMap.get("my/path/potatoes/notsomething".split("/"), HttpMethod.GET);
		assertEquals(parentContextExe, actual);
	}

	@Test
	public void notFoundController_withinPath() throws NoSuchMethodException {
		controllerExecutionMap.put(context, new Request(HttpMethod.GET, "/my/path"), execution3);

		context.setNotFoundController(ControllerExecutionMapTest.class);
		context.applyMappings();

		ControllerExecution actual = controllerExecutionMap.get("my/path/potatoes/notsomething".split("/"), HttpMethod.GET);
		assertEquals(ControllerExecutionMapTest.class.getMethod("notFoundController"), actual.getMethod());
	}

	@Test
	public void notFoundController_withWildCards() throws NoSuchMethodException {
		controllerExecutionMap.put(context, new Request(HttpMethod.GET, "/my/path/{wild}"), execution3);

		context.setNotFoundController(ControllerExecutionMapTest.class);
		context.applyMappings();
		ControllerExecution actual = controllerExecutionMap.get("my/path/potatoes/notsomething".split("/"), HttpMethod.GET);
		assertEquals(ControllerExecutionMapTest.class.getMethod("notFoundController"), actual.getMethod());
	}

	@Test
	public void notFoundController_setOnLowerPath() throws NoSuchMethodException {
		otherContext.parentContext = context; // only makes sense if we assume that the parent context of /my/path/{wild1} is /my/path
		context.setNotFoundController(ControllerExecutionMapTest.class);
		context.applyMappings();

		controllerExecutionMap.put(context, new Request(HttpMethod.GET, "/my/path"), execution2);
		controllerExecutionMap.put(otherContext, new Request(HttpMethod.GET, "/my/path/{wild1}"), execution1);

		ControllerExecution actual = controllerExecutionMap.get("my/path/potatoes/notsomething".split("/"), HttpMethod.GET);
		assertEquals(ControllerExecutionMapTest.class.getMethod("notFoundController"), actual.getMethod());
	}

	@Test
	public void notFoundController_setOnHigherPath() throws NoSuchMethodException {
		controllerExecutionMap.put(context, new Request(HttpMethod.GET, "/my/path"), execution2);
		controllerExecutionMap.put(otherContext, new Request(HttpMethod.GET, "/my/path/{wild1}"), execution1);

		otherContext.setNotFoundController(ControllerExecutionMapTest.class);
		otherContext.applyMappings();
		ControllerExecution actual = controllerExecutionMap.get("my/path/potatoes/notsomething".split("/"), HttpMethod.GET);

		assertEquals(ControllerExecutionMapTest.class.getMethod("notFoundController"), actual.getMethod());
	}

	@Test
	public void notFoundController_noController() {
		controllerExecutionMap.put(context, new Request(HttpMethod.GET, "/my/path"), execution2);
		controllerExecutionMap.put(otherContext, new Request(HttpMethod.GET, "/my/path/{wild1}"), execution1);

		ControllerExecution actual = controllerExecutionMap.get("my/path/potatoes/notsomething".split("/"), HttpMethod.GET);
		assertEquals(parentContextExe, actual);
	}

	@NotFoundController
	public void notFoundController() {

	}
}