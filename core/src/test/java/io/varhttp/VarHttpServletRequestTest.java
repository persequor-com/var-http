package io.varhttp;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class VarHttpServletRequestTest {

	VarHttpServletRequest request;
	@Mock
	private HttpExchange ex;
	@Mock
	private ServletInputStream inputStream;
	@Mock
	private HttpServletRequest innerRequest;
	private Map<String, String[]> postData = new HashMap<>();
	@Mock
	private Headers headers;

	@Before
	public void setup() {
		request = new VarHttpServletRequest(innerRequest, ex, postData, inputStream);
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
}