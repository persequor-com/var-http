package io.varhttp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class VarServletTest {

	private VarServlet servlet;
	@Mock
	VarConfig varConfig;
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

	private ExceptionRegistry exceptionRegistry = new ExceptionRegistry();

	@Before
	public void setup() {
		servlet = new VarServlet(varConfig, parameterHandler, controllerMapper, objectFactory, controllerFilter, exceptionRegistry);
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
	public void handleGet_withRedirectAndQueryString_queryStringDoesNotAffectRedirecting() throws IOException {
		VarHttpServletRequest request = mock(VarHttpServletRequest.class);
		when(request.getMethod()).thenReturn("GET");
		when(request.getPathInfo()).thenReturn("/redirected-test");
		when(request.getQueryString()).thenReturn("param1=value1&param2=value2");
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
	public void handleGet_withRedirectAndQueryString_queryStringIsPreserved() throws IOException, InterruptedException {
		VarHttpServletRequest request = mock(VarHttpServletRequest.class);
		when(request.getMethod()).thenReturn("GET");
		when(request.getPathInfo()).thenReturn("/redirected-test");
		when(request.getQueryString()).thenReturn("param1=value1&param2=value2");
		when(request.getParameterMap()).thenAnswer(invocation -> {
			Map<String, List<String>> stringListMap = HttpHelper.parseQueryString(request.getQueryString());
			return stringListMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toArray(new String[0])));
		});
		when(context.getParameterHandler()).thenCallRealMethod();
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
		CountDownLatch latch = new CountDownLatch(1);
		doAnswer(invocation -> {
			ControllerContext context = (ControllerContext) invocation.getArguments()[0];
			if (context.getParameters().get("param1").equals("value1") && context.getParameters().get("param2").equals("value2")) {
				latch.countDown();
			}
			return null;
		}).when(usedController).execute(any());

		servlet.redirect("/redirected-test", "/test");

		servlet.doGet(request, response);

		assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));

		verify(usedController, times(1)).execute(any());
		verify(unusedController, never()).execute(any());
		verify(response, times(0)).setStatus(404);
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