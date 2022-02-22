package io.varhttp;

import io.varhttp.test.ControllerInTestPackage;
import io.varhttp.test.sub.ControllerInSub;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ControllerMapperTest {
	ControllerMapper mapper = new ControllerMapper(new AnnotationsHelper());
	@Mock
	private VarConfigurationContext context ;

	@Test
	public void controllerInSubPackage() throws Throwable {
		when(context.getControllerMatchers()).thenReturn(Arrays.asList(new ControllerMatcher() {
			@Override
			public Optional<ControllerMatch> find(Method method) {
				return Optional.of(new ControllerMatch(method, "/", Collections.emptySet(), "text/html"));
			}
		}));

		mapper.map(context, ControllerInTestPackage.class.getPackage().getName());

		verify(context).addExecution(eq(ControllerInTestPackage.class), eq(ControllerInTestPackage.class.getMethod("controller")), anyString(), anyString(), any(ControllerMatch.class), any(VarConfigurationContext.class));
		verify(context).addExecution(eq(ControllerInSub.class), eq(ControllerInSub.class.getMethod("controller")), anyString(), anyString(), any(ControllerMatch.class), any(VarConfigurationContext.class));
	}
}