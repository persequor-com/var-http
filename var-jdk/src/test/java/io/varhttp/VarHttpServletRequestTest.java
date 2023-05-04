package io.varhttp;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsServer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class VarHttpServletRequestTest {

	JdkHttpServletRequest request;
	@Mock
	private HttpExchange ex;
	@Mock
	private ServletInputStream inputStream;
	@Mock
	private Headers headers;
	@Mock
	private VarConfig config;
	@Mock
	private HttpsServer httpsServer;
	@Mock
	private HttpServer httpServer;
	@Mock
	private HttpContext httpContext;
	private Map<String, String[]> postData = new HashMap<>();

	@Before
	public void setup() {
		request = new JdkHttpServletRequest(ex, postData, inputStream, new JdkServletContext(ex), config);
		Mockito.when(ex.getRequestHeaders()).thenReturn(headers);
	}

	@Test
	public void getCookies_happyPath() {
		Mockito.when(headers.getFirst("Cookie")).thenReturn("my=cookie; is=yours");
		Cookie[] actual = request.getCookies();
		assertEquals(2, actual.length);
		assertEquals("my", actual[0].getName());
		assertEquals("cookie", actual[0].getValue());
		assertEquals("is", actual[1].getName());
		assertEquals("yours", actual[1].getValue());
	}

	@Test
	public void getCookies_null() {
		Mockito.when(headers.getFirst("Cookie")).thenReturn(null);
		Cookie[] actual = request.getCookies();
		assertEquals(0, actual.length);
	}

	@Test
	public void isSecure_With_VarConfig() {
		Mockito.when(config.isForceRequestSecure()).thenReturn(true);
		assertTrue(request.isSecure());
	}

	@Test
	public void isSecure_XForwardedProto() {
		Mockito.when(config.isForceRequestSecure()).thenReturn(false);

		Mockito.when(headers.getFirst("X-Forwarded-Proto")).thenReturn("https");

		assertTrue(request.isSecure());

		Mockito.when(headers.getFirst("X-Forwarded-Proto")).thenReturn("http");

		assertFalse(request.isSecure());
	}

	@Test
	public void isSecure_requestContext() {
		Mockito.when(config.isForceRequestSecure()).thenReturn(false);
		Mockito.when(ex.getHttpContext()).thenReturn(httpContext);

		Mockito.when(httpContext.getServer()).thenReturn(httpsServer);
		assertTrue(request.isSecure());

		Mockito.when(httpContext.getServer()).thenReturn(httpServer);
		assertFalse(request.isSecure());
	}

	@Test
	public void getCookies_noEqualSign() {
		Mockito.when(headers.getFirst("Cookie")).thenReturn("I-am-the-cookie");
		Cookie[] actual = request.getCookies();
		assertEquals(0, actual.length);
	}
}
