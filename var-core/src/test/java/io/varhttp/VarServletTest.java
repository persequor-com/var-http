package io.varhttp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.ServletOutputStream;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class VarServletTest {

	private VarServlet servlet;
	@Mock
	private ParameterHandler parameterHandler;
	@Mock
	private ControllerMapper controllerMapper;
	@Mock
	private ObjectFactory objectFactory;
	@Mock
	private ControllerFilter controllerFilter;
	@Mock
	private VarConfigurationContext context;

	@Before
	public void setup() {
		servlet = new VarServlet(parameterHandler, controllerMapper, objectFactory, controllerFilter);
	}

	@Test
	public void handleGet_happyPath() throws IOException {
		VarHttpServletRequest request = mock(VarHttpServletRequest.class);
		when(request.getMethod()).thenReturn("GET");
		when(request.getPathInfo()).thenReturn("/test");
		when(request.getQueryString()).thenReturn("");
		when(request.getContentType()).thenReturn("application/json");
		when(request.getCharacterEncoding()).thenReturn("UTF-8");
		when(request.getServletPath()).thenReturn("");
		when(request.getContextPath()).thenReturn("");
		when(request.getRequestURI()).thenReturn("/test");
		when(request.getRequestURL()).thenReturn(new StringBuffer("/test"));
		VarHttpServletResponse response = mock(VarHttpServletResponse.class);

		ControllerExecution usedController = mock(ControllerExecution.class);
		servlet.executions.put(context, new Request(HttpMethod.GET, "/test"), usedController);
		ControllerExecution unusedController = mock(ControllerExecution.class);
		servlet.executions.put(context, new Request(HttpMethod.GET, "/other"), unusedController);

		when(response.getOutputStream()).thenReturn(mock(ServletOutputStream.class));

		servlet.doGet(request, response);

		verify(usedController, times(1)).execute(any());
		verify(unusedController, never()).execute(any());
		verify(response, times(0)).setStatus(404);
	}

	@Test
	public void handleGet_withRedirect_happyPath() throws IOException {
		VarHttpServletRequest request = mock(VarHttpServletRequest.class);
		when(request.getMethod()).thenReturn("GET");
		when(request.getPathInfo()).thenReturn("/redirected-test");
		when(request.getQueryString()).thenReturn("");
		when(request.getContentType()).thenReturn("application/json");
		when(request.getCharacterEncoding()).thenReturn("UTF-8");
		when(request.getServletPath()).thenReturn("");
		when(request.getContextPath()).thenReturn("");
		when(request.getRequestURI()).thenReturn("/redirected-test");
		when(request.getRequestURL()).thenReturn(new StringBuffer("/redirected-test"));
		VarHttpServletResponse response = mock(VarHttpServletResponse.class);

		ControllerExecution usedController = mock(ControllerExecution.class);
		servlet.executions.put(context, new Request(HttpMethod.GET, "/test"), usedController);
		ControllerExecution unusedController = mock(ControllerExecution.class);
		servlet.executions.put(context, new Request(HttpMethod.GET, "/other"), unusedController);

		when(response.getOutputStream()).thenReturn(mock(ServletOutputStream.class));

		servlet.redirect("/redirected-test", "/test");

		servlet.doGet(request, response);

		verify(usedController, times(1)).execute(any());
		verify(unusedController, never()).execute(any());
		verify(response, times(0)).setStatus(404);
	}

	@Test
	public void handleGet_withRedirect_toUnregisteredEndpoint() throws IOException {
		VarHttpServletRequest request = mock(VarHttpServletRequest.class);
		when(request.getMethod()).thenReturn("GET");
		when(request.getPathInfo()).thenReturn("/redirected-test");
		when(request.getQueryString()).thenReturn("");
		when(request.getContentType()).thenReturn("application/json");
		when(request.getCharacterEncoding()).thenReturn("UTF-8");
		when(request.getServletPath()).thenReturn("");
		when(request.getContextPath()).thenReturn("");
		when(request.getRequestURI()).thenReturn("/redirected-test");
		when(request.getRequestURL()).thenReturn(new StringBuffer("/redirected-test"));
		VarHttpServletResponse response = mock(VarHttpServletResponse.class);

		ControllerExecution usedController = mock(ControllerExecution.class);
		servlet.executions.put(context, new Request(HttpMethod.GET, "/test"), usedController);
		ControllerExecution unusedController = mock(ControllerExecution.class);
		servlet.executions.put(context, new Request(HttpMethod.GET, "/other"), unusedController);

		when(response.getOutputStream()).thenReturn(mock(ServletOutputStream.class));

		servlet.redirect("/redirected-test", "/unregistered");

		servlet.doGet(request, response);

		verify(usedController, never()).execute(any());
		verify(unusedController, never()).execute(any());
		verify(response, times(1)).setStatus(404);
	}

	@Test
	public void handlePost_withRedirect_happyPath() throws IOException {
		VarHttpServletRequest request = mock(VarHttpServletRequest.class);
		when(request.getMethod()).thenReturn("POST");
		when(request.getPathInfo()).thenReturn("/redirected-test");
		when(request.getQueryString()).thenReturn("");
		when(request.getContentType()).thenReturn("application/json");
		when(request.getCharacterEncoding()).thenReturn("UTF-8");
		when(request.getServletPath()).thenReturn("");
		when(request.getContextPath()).thenReturn("");
		when(request.getRequestURI()).thenReturn("/redirected-test");
		when(request.getRequestURL()).thenReturn(new StringBuffer("/redirected-test"));
		VarHttpServletResponse response = mock(VarHttpServletResponse.class);

		ControllerExecution usedController = mock(ControllerExecution.class);
		servlet.executions.put(context, new Request(HttpMethod.POST, "/test"), usedController);
		ControllerExecution unusedController = mock(ControllerExecution.class);
		servlet.executions.put(context, new Request(HttpMethod.POST, "/other"), unusedController);

		when(response.getOutputStream()).thenReturn(mock(ServletOutputStream.class));

		servlet.redirect("/redirected-test", "/test");

		servlet.doPost(request, response);

		verify(usedController, times(1)).execute(any());
		verify(unusedController, never()).execute(any());
		verify(response, times(0)).setStatus(404);
	}

}