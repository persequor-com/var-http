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

	@Test(expected = RuntimeException.class)
	public void duplicate() {
		executionMap.put(new Request(HttpMethod.GET, "/my/path"), execution1);
		executionMap.put(new Request(HttpMethod.GET, "/my/path"), execution2);
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

	@Test(expected = RuntimeException.class)
	public void deepWildcard_clashesWithNonWildcard() {
		executionMap.put(new Request(HttpMethod.GET, "/my/path/{wild1}/{wild2}"), execution1);
		executionMap.put(new Request(HttpMethod.GET, "/my/path/{wild1}/sub"), execution2);
	}

	@Test(expected = RuntimeException.class)
	public void deepWildcard_clashesWithNonWildcard_reverseOrder() {
		executionMap.put(new Request(HttpMethod.GET, "/my/path/{wild1}/sub"), execution2);
		executionMap.put(new Request(HttpMethod.GET, "/my/path/{wild1}/{wild2}"), execution1);
	}
}