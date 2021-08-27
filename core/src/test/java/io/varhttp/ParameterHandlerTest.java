package io.varhttp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ParameterHandlerTest {
	@InjectMocks
	private ParameterHandler parameterHandler;

	@Mock
	private Serializer serializer;

	@Test(expected = VarInitializationException.class)
	public void hasPathVariableAnnotation() throws Throwable {
		parameterHandler.initializeHandlers(getClass().getMethod("withMissingPathVariableAnnotation"), "my/{path}", "");
	}

	@Test(expected = VarInitializationException.class)
	public void missingPathVariableAnnotation() throws Throwable {
		parameterHandler.initializeHandlers(getClass().getMethod("withMissingPathVariableAnnotation"), "my/{path}", "");
	}

	@Controller(path = "isNotUsedInThisTest")
	public void withPathVariableAnnotation(@PathVariable(name = "path") String myVar) {
		// Does nothing
	}

	@Controller(path = "isNotUsedInThisTest")
	public void withMissingPathVariableAnnotation() {
		// Does nothing
	}
}