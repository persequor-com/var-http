package io.varhttp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ExecutionMapTest {

	ExecutionMap executionMap;
	@Mock
	private ControllerExecution execution1;
	@Mock
	private ControllerExecution execution2;
	@Mock
	private ControllerExecution execution3;

	@Before
	public void setup() {
		executionMap = new ExecutionMap();
	}

	@Test
	public void happy() {
		executionMap.put(new Request(HttpMethod.GET, "/my/path"), execution1);
		executionMap.put(new Request(HttpMethod.GET, "/my/path2"), execution2);

		ControllerExecution actual = executionMap.get("my/path".split("/"), HttpMethod.GET);
		assertSame(execution1, actual);

		actual = executionMap.get("my/path2".split("/"), HttpMethod.GET);
		assertSame(execution2, actual);
	}

	@Test
	public void notADuplicate_ifDifferentHttpMethods() {
		executionMap.put(new Request(HttpMethod.PUT, "/my/path"), execution1);
		executionMap.put(new Request(HttpMethod.GET, "/my/path"), execution2);
	}


	@Test
	public void notADuplicate_ifDifferentHttpMethods_forBaseUrl() {
		executionMap.put(new Request(HttpMethod.PUT, "/"), execution1);
		executionMap.put(new Request(HttpMethod.GET, "/"), execution2);

		ControllerExecution actual = executionMap.get(new String[]{}, HttpMethod.GET);
		assertSame(execution2, actual);
	}

	@Test
	public void passingEmptyStringPath() {
		executionMap.put(new Request(HttpMethod.GET, "/"), execution2);

		ControllerExecution actual = executionMap.get(new String[]{""}, HttpMethod.GET);
		assertSame(execution2, actual);
	}

	@Test(expected = RuntimeException.class)
	public void duplicate() {
		executionMap.put(new Request(HttpMethod.GET, "/my/path"), execution1);
		executionMap.put(new Request(HttpMethod.GET, "/my/path"), execution2);
	}

	@Test(expected = RuntimeException.class)
	public void duplicate_forBaseUrl() {
		executionMap.put(new Request(HttpMethod.GET, "/"), execution1);
		executionMap.put(new Request(HttpMethod.GET, "/"), execution2);
	}

	@Test
	public void simpleWildcard() {
		executionMap.put(new Request(HttpMethod.GET, "/my/path/{wild1}"), execution1);
		executionMap.put(new Request(HttpMethod.GET, "/my/path/{wild1}/sub"), execution2);

		ControllerExecution actual = executionMap.get("my/path/muh".split("/"), HttpMethod.GET);
		assertSame(execution1, actual);

		actual = executionMap.get("my/path/muh2/sub".split("/"), HttpMethod.GET);
		assertSame(execution2, actual);
	}

	@Test
	public void wildcardAndEmptyOnSameHttpMethod() {
		executionMap.put(new Request(HttpMethod.GET, "/my/path"), execution2);
		executionMap.put(new Request(HttpMethod.GET, "/my/path/{wild1}"), execution1);

		ControllerExecution actual = executionMap.get("my/path".split("/"), HttpMethod.GET);
		assertSame(execution2, actual);

		actual = executionMap.get("my/path/blah".split("/"), HttpMethod.GET);
		assertSame(execution1, actual);
	}

	@Test
	public void wildcardAndEmptyOnSameHttpMethod_reversed() {
		executionMap.put(new Request(HttpMethod.GET, "/my/path/{wild1}"), execution1);
		executionMap.put(new Request(HttpMethod.GET, "/my/path"), execution2);

		ControllerExecution actual = executionMap.get("my/path".split("/"), HttpMethod.GET);
		assertSame(execution2, actual);

		actual = executionMap.get("my/path/blah".split("/"), HttpMethod.GET);
		assertSame(execution1, actual);
	}

	@Test
	public void deepWildcard_clashesWithNonWildcard() {
		executionMap.put(new Request(HttpMethod.GET, "/my/path/{wild1}/{wild2}"), execution1);
		executionMap.put(new Request(HttpMethod.GET, "/my/path/{wild1}/sub"), execution2);

		ControllerExecution actual = executionMap.get("my/path/something/something".split("/"), HttpMethod.GET);
		assertSame(execution1, actual);

		actual = executionMap.get("my/path/something/sub".split("/"), HttpMethod.GET);
		assertSame(execution2, actual);
	}

	@Test
	public void deepWildcard_clashesWithNonWildcard_reverseOrder() {
		executionMap.put(new Request(HttpMethod.GET, "/my/path/{wild1}/sub"), execution2);
		executionMap.put(new Request(HttpMethod.GET, "/my/path/{wild1}/{wild2}"), execution1);

		ControllerExecution actual = executionMap.get("my/path/something/something".split("/"), HttpMethod.GET);
		assertSame(execution1, actual);

		actual = executionMap.get("my/path/something/sub".split("/"), HttpMethod.GET);
		assertSame(execution2, actual);
	}

	@Test
	public void multilevelWildcard() {
		executionMap.put(new Request(HttpMethod.GET, "/*"), execution1);
		executionMap.put(new Request(HttpMethod.GET, "/my/path"), execution2);
		executionMap.put(new Request(HttpMethod.GET, "/other/path"), execution3);

		ControllerExecution actual = executionMap.get("my/path".split("/"), HttpMethod.GET);
		assertSame(execution2, actual);

		actual = executionMap.get("other/path".split("/"), HttpMethod.GET);
		assertSame(execution3, actual);

		actual = executionMap.get("something/else/entirely".split("/"), HttpMethod.GET);
		assertSame(execution1, actual);

		actual = executionMap.get("and/yet/another/different/path".split("/"), HttpMethod.GET);
		assertSame(execution1, actual);
	}

	@Test
	public void multilevelWildcard_reversed() {
		executionMap.put(new Request(HttpMethod.GET, "/other/path"), execution3);
		executionMap.put(new Request(HttpMethod.GET, "/my/path"), execution2);
		executionMap.put(new Request(HttpMethod.GET, "/*"), execution1);

		ControllerExecution actual = executionMap.get("my/path/something/something".split("/"), HttpMethod.GET);
		assertSame(execution2, actual);

		actual = executionMap.get("other/path".split("/"), HttpMethod.GET);
		assertSame(execution3, actual);

		actual = executionMap.get("something/else/entirely".split("/"), HttpMethod.GET);
		assertSame(execution1, actual);

		actual = executionMap.get("and/yet/another/different/path".split("/"), HttpMethod.GET);
		assertSame(execution1, actual);
	}
}