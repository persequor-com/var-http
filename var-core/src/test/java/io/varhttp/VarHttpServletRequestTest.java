package io.varhttp;

import com.sun.net.httpserver.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class VarHttpServletRequestTest {

	VarHttpServletRequest request;
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
		request = new VarHttpServletRequest(ex, postData, inputStream, new VarServletContext(ex), config);
		when(ex.getRequestHeaders()).thenReturn(headers);
	}

	@Test
	public void getCookies_happyPath() {
		when(headers.getFirst("Cookie")).thenReturn("my=cookie; is=yours");
		Cookie[] actual = request.getCookies();
		assertEquals(2, actual.length);
		assertEquals("my", actual[0].getName());
		assertEquals("cookie", actual[0].getValue());
		assertEquals("is", actual[1].getName());
		assertEquals("yours", actual[1].getValue());
	}

	@Test
	public void getCookies_null() {
		when(headers.getFirst("Cookie")).thenReturn(null);
		Cookie[] actual = request.getCookies();
		assertEquals(0, actual.length);
	}

	@Test
	public void isSecure_With_VarConfig() {
		when(config.isForceRequestSecure()).thenReturn(true);
		assertTrue(request.isSecure());
	}

	@Test
	public void isSecure_XForwardedProto() {
		when(config.isForceRequestSecure()).thenReturn(false);

		when(headers.getFirst("X-Forwarded-Proto")).thenReturn("https");

		assertTrue(request.isSecure());

		when(headers.getFirst("X-Forwarded-Proto")).thenReturn("http");

		assertFalse(request.isSecure());
	}

	@Test
	public void isSecure_requestContext() {
		when(config.isForceRequestSecure()).thenReturn(false);
		when(ex.getHttpContext()).thenReturn(httpContext);

		when(httpContext.getServer()).thenReturn(httpsServer);
		assertTrue(request.isSecure());

		when(httpContext.getServer()).thenReturn(httpServer);
		assertFalse(request.isSecure());
	}

	@Test
	public void getCookies_noEqualSign() {
		when(headers.getFirst("Cookie")).thenReturn("I-am-the-cookie");
		Cookie[] actual = request.getCookies();
		assertEquals(0, actual.length);
	}
}
