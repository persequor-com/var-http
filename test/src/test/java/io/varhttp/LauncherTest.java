package io.varhttp;

import io.odinjector.OdinJector;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class LauncherTest {
	static Launcher launcher;
	static Thread thread;

	@BeforeClass
	public static void setup() {
		OdinJector odinJector = OdinJector.create().addContext(new OdinContext(new VarConfig().setPort(8088)));
		launcher = odinJector.getInstance(Launcher.class);
		thread = new Thread(launcher);
		thread.run();
	}

	@AfterClass
	public static void teardown() {
		launcher.stop();
	}

	@Test
	public void simple() throws Throwable {
		HttpURLConnection con = HttpClient.get("http://localhost:8088/my-test", "");

		StringBuffer content = HttpClient.readContent(con);
		Map<String, List<String>> headers = HttpClient.readHeaders(con);

		assertTrue(headers.containsKey("Content-type"));
		assertEquals("text/plain", headers.get("Content-type").get(0));

		assertEquals("Simple string", content.toString());
	}

	@Test
	public void requestingUnsupportedContentType() throws Throwable {
		HttpURLConnection con = HttpClient.get("http://localhost:8088/my-test", "");
		con.setRequestProperty("Accept", "my/custom");

		try {
			HttpClient.readContent(con);
			fail();
		} catch (IOException e) {
			assertTrue(e.getMessage().startsWith("Server returned HTTP response code: 415"));
		}
	}

	@Test
	public void notSettingAcceptHeaderContentType_willFallbackToServerDefault() throws Throwable {
		HttpURLConnection con = HttpClient.get("http://localhost:8088/my-test", "");
		con.setRequestProperty("Accept", "");

		HttpClient.readContent(con);
		Map<String, List<String>> headers = HttpClient.readHeaders(con);
		assertEquals("text/plain", headers.get("Content-type").get(0));
	}

	@Test
	public void pathVariable() throws Throwable {
		HttpURLConnection con = HttpClient.get("http://localhost:8088/pathVar/my-string", "");

		StringBuffer content = HttpClient.readContent(con);

		assertEquals("my-string", content.toString());
	}

	@Test
	public void pathVariableMultiple() throws Throwable {
		HttpURLConnection con = HttpClient.get("http://localhost:8088/pathVar/string1/string2/string3", "");

		StringBuffer content = HttpClient.readContent(con);

		assertEquals("string1-string2-string3", content.toString());
	}

	@Test
	public void requestParameter() throws Throwable {
		HttpURLConnection con = HttpClient.get("http://localhost:8088/requestParameter","var=my");

		StringBuffer content = HttpClient.readContent(con);

		assertEquals("my", content.toString());
	}


	@Test
	public void serializedReturnObject() throws Throwable {
		HttpURLConnection con = HttpClient.get("http://localhost:8088/my-test-serialized", "");

		StringBuffer content = HttpClient.readContent(con);

		assertEquals("{\"string\":\"Simple string\"}", content.toString());
	}

	@Test
	public void perfSimple() throws Throwable {
		int reps = 100;
		long s = System.currentTimeMillis();
		for(int i=0;i<reps;i++) {
			HttpURLConnection con = HttpClient.get("http://localhost:8088/my-test", "");

			HttpClient.readContent(con);
		}
		System.out.println("avg time to run: "+((System.currentTimeMillis()-s)/(reps*1.0d)));

	}

	@Test
	public void headers() throws Throwable {
		HttpURLConnection con = HttpClient.get("http://localhost:8088/header", "");
		con.addRequestProperty("My", "Input header");

		String response = HttpClient.readContent(con).toString();
		assertEquals("Input header", con.getHeaderField("My"));
		assertEquals("text/plainish", con.getHeaderField("Content-Type"));
		assertEquals("muh", response);
	}

	@Test
	public void headerPathInfo() throws Throwable {
		HttpURLConnection con = HttpClient.get("http://localhost:8088/header-path-info/xxx?key=value", "");

		String response = HttpClient.readContent(con).toString();
		assertEquals("/header-path-info/xxx", response);
	}

	@Test
	public void servletRequest() throws Throwable {
		HttpURLConnection con = HttpClient.get("http://localhost:8088/http-servlet-request/xxx?key=value", "");

		String response = HttpClient.readContent(con).toString();
		assertEquals("/http-servlet-request/xxx", response);
	}

	@Test
	public void rootController() throws Throwable {
		HttpURLConnection con = HttpClient.get("http://localhost:8088/", "");

		String response = HttpClient.readContent(con).toString();
		assertEquals("Who am i", response);
	}

	@Test
	public void prefixedController() throws Throwable {
		HttpURLConnection con = HttpClient.get("http://localhost:8088/packageprefix/classprefix/controller", "");

		String response = HttpClient.readContent(con).toString();
		assertEquals("prefixed", response);
	}

	@Test
	public void defaultValueInParameter() throws Throwable {
		HttpURLConnection con = HttpClient.get("http://localhost:8088/defaultValue?param2=cat", "");

		String response = HttpClient.readContent(con).toString();
		assertEquals("muh-cat", response);
	}

	@Test
	public void optionalBody() throws Throwable {
		HttpURLConnection con = HttpClient.post("http://localhost:8088/optionalBody", null);

		String response = HttpClient.readContent(con).toString();
		assertEquals("Nothing passed in", response);
	}

	@Test
	public void primitiveParameters() throws Throwable {
		HttpURLConnection con = HttpClient.post("http://localhost:8088/primitives?bool=true&integer=43&longer=234423&doubler=0.4&floater=0.43", null);

		String response = HttpClient.readContent(con).toString();
		assertEquals("true:43:234423:0.4:0.43", response);
	}

	@Test
	public void primitiveParameters_default() throws Throwable {
		HttpURLConnection con = HttpClient.post("http://localhost:8088/primitives", null);

		String response = HttpClient.readContent(con).toString();
		assertEquals("false:0:0:0.0:0.0", response);
	}


	@Test
	public void primitivesBoxedParameters() throws Throwable {
		HttpURLConnection con = HttpClient.post("http://localhost:8088/primitivesBoxed?bool=true&integer=43&longer=234423&doubler=0.4&floater=0.43", null);

		String response = HttpClient.readContent(con).toString();
		assertEquals("true:43:234423:0.4:0.43", response);
	}

	@Test
	public void primitivesBoxedParameters_default() throws Throwable {
		HttpURLConnection con = HttpClient.post("http://localhost:8088/primitivesBoxed", null);

		String response = HttpClient.readContent(con).toString();
		assertEquals("null:null:null:null:null", response);
	}

	@Test
	public void redirectRelative() throws Throwable {
		HttpURLConnection con = HttpClient.post("http://localhost:8088/redirects/redirectRelative", null);

		Map<String, List<String>> headers = HttpClient.readHeaders(con);
		assertEquals("/redirects/target", headers.get("Location").get(0));
	}

	@Test
	public void redirect() throws Throwable {
		HttpURLConnection con = HttpClient.post("http://localhost:8088/redirects/redirect", null);

		Map<String, List<String>> headers = HttpClient.readHeaders(con);
		assertEquals("/redirects/target", headers.get("Location").get(0));
	}

	@Test
	public void redirectUrl() throws Throwable {
		HttpURLConnection con = HttpClient.post("http://localhost:8088/redirects/url", null);

		Map<String, List<String>> headers = HttpClient.readHeaders(con);
		assertEquals("http://github.com", headers.get("Location").get(0));
	}

	@Test
	public void requestParametersGet() throws Throwable {
		HttpURLConnection con = HttpClient.get("http://localhost:8088/requestParameters?what=theFuture&where=here", "");

		String response = HttpClient.readContent(con).toString();
		assertEquals("theFuture is null", response);
	}

	@Test
	public void requestParametersPost() throws Throwable {
		HttpURLConnection con = HttpClient.post("http://localhost:8088/requestParameters", "what=theFuture&where=here");

		String response = HttpClient.readContent(con).toString();
		assertEquals("theFuture is null", response);
	}

	@Test
	public void listController() throws Throwable {
		HttpURLConnection con = HttpClient.post("http://localhost:8088/listController?list=Muh&list=Miaw", "");

		String response = HttpClient.readContent(con).toString();
		assertEquals("[\"Muh\",\"Miaw\"]", response);
	}


	@Test
	public void listObject() throws Throwable {
		HttpURLConnection con = HttpClient.post("http://localhost:8088/listObject", "[{\"id\":\"id1\",\"name\":\"name1\"},{\"id\":\"id2\",\"name\":\"name2\"}]", "application/json");

		String response = HttpClient.readContent(con).toString();
		assertEquals("[{\"id\":\"id1\",\"name\":\"name1!\"},{\"id\":\"id2\",\"name\":\"name2!\"}]", response);
	}


	@Test
	public void listController_listNotSet() throws Throwable {
		HttpURLConnection con = HttpClient.post("http://localhost:8088/listController", "");

		String response = HttpClient.readContent(con).toString();
		assertEquals("", response);
	}

	@Test
	public void dates() throws Throwable {
		HttpURLConnection con = HttpClient.post("http://localhost:8088/dates?date=2020-01-01T12:30:15Z&zonedDateTime=2020-01-01T12:30:45Z&localDate=2020-01-10", "");

		String response = HttpClient.readContent(con).toString();
		assertEquals("2020-01-01T12:30:15Z-2020-01-01T12:30:45Z-2020-01-10", response);
	}

	@Test
	public void requestBodyString() throws Throwable {
		HttpURLConnection con = HttpClient.post("http://localhost:8088/requestBodyString?otherParameter=param", "This is a string, the only string my friend", "text/plain");

		String response = HttpClient.readContent(con).toString();
		assertEquals("This is a string, the only string my friend", response);
	}

	@Test
	public void requestBodyInputStream() throws Throwable {
		HttpURLConnection con = HttpClient.post("http://localhost:8088/requestBodyInputStream", "I AM THE BODY", "text/plain");

		String response = HttpClient.readContent(con).toString();
		assertEquals("I AM THE BODY", response);
	}

	@Test
	public void responseStream_getOutputStream_contentType() throws Throwable {
		HttpURLConnection con = HttpClient.post("http://localhost:8088/responseStream_getOutputStream_contentType", "");

		Map<String, List<String>> headers = HttpClient.readHeaders(con);
		String response = HttpClient.readContent(con).toString();
		assertEquals(1, headers.get("Content-type").size());
		assertEquals("text/test", headers.get("Content-type").get(0));
		assertEquals("tadaaa", response);
	}

	@Test
	public void getOutputStream_addiionalContentType() throws Throwable {
		HttpURLConnection con = HttpClient.post("http://localhost:8088/getOutputStream_addiionalContentType", "");

		Map<String, List<String>> headers = HttpClient.readHeaders(con);
		String response = HttpClient.readContent(con).toString();
		assertEquals(1, headers.get("Content-type").size());
		assertEquals("text/test", headers.get("Content-type").get(0));
		assertEquals("tadaaa", response);
	}

	@Test
	public void returnJavascriptString() throws Throwable {
		HttpURLConnection con = HttpClient.post("http://localhost:8088/returnJavascriptString", "");

		Map<String, List<String>> headers = HttpClient.readHeaders(con);
		String response = HttpClient.readContent(con).toString();
		assertEquals(1, headers.get("Content-type").size());
		assertEquals("application/javascript", headers.get("Content-type").get(0));
		assertEquals("alert('hello darkness my old friend')", response);
	}

	@Test
	public void javascriptInResponseStream() throws Throwable {
		HttpURLConnection con = HttpClient.post("http://localhost:8088/javascriptInResponseStream", "");

		Map<String, List<String>> headers = HttpClient.readHeaders(con);
		String response = HttpClient.readContent(con).toString();
		assertEquals(1, headers.get("Content-type").size());
		assertEquals("application/javascript", headers.get("Content-type").get(0));
		assertEquals("alert('hello darkness my old friend')", response);
	}

	@Test
	public void headController() throws Throwable {
		HttpURLConnection con = HttpClient.head("http://localhost:8088/headController");

		Map<String, List<String>> headers = HttpClient.readHeaders(con);
	}

	@Test
	public void altControllerAnnotation() throws Throwable {
		HttpURLConnection con = HttpClient.get("http://localhost:8088/altControllerAnnotation","");

		String response = HttpClient.readContent(con).toString();
		assertEquals("Kilroy was here", response);
	}

	@Test
	public void enumParameter() throws Throwable {
		HttpURLConnection con = HttpClient.get("http://localhost:8088/enumParameter/Kilroy","");

		String response = HttpClient.readContent(con).toString();
		assertEquals("Kilroy was here", response);
	}


	@Test
	public void serializedReturnObject_toAcceptedContentType() throws Throwable {
		HttpURLConnection con = HttpClient.get("http://localhost:8088/my-test-serialized", "");
		con.setRequestProperty("Accept", "application/xml");

		StringBuffer content = HttpClient.readContent(con);
		final Map<String, List<String>> headers = HttpClient.readHeaders(con);

		assertTrue(headers.containsKey("Content-type"));
		assertEquals(headers.get("Content-type").get(0), "application/xml");

		assertEquals("<TestResponse><string>Simple string</string></TestResponse>", content.toString());
	}

	@Test
	public void serializedReturnObject_toAcceptedContentType_applicationJson() throws Throwable {
		HttpURLConnection con = HttpClient.get("http://localhost:8088/my-test-serialized", "");
		con.setRequestProperty("Accept", "application/json");

		StringBuffer content = HttpClient.readContent(con);
		final Map<String, List<String>> headers = HttpClient.readHeaders(con);

		assertTrue(headers.containsKey("Content-type"));
		assertEquals(headers.get("Content-type").get(0), "application/json");

		assertEquals("{\"string\":\"Simple string\"}", content.toString());
	}

	@Test
	public void serializedReturnObject_toAcceptedContentType_withNoAccept() throws Throwable {
		HttpURLConnection con = HttpClient.get("http://localhost:8088/my-test-serialized", "");

		StringBuffer content = HttpClient.readContent(con);
		final Map<String, List<String>> headers = HttpClient.readHeaders(con);

		assertTrue(headers.containsKey("Content-type"));
		assertEquals(headers.get("Content-type").get(0), "application/json");

		assertEquals("{\"string\":\"Simple string\"}", content.toString());
	}

	@Test(expected = IOException.class)
	public void serializedReturnObject_toAcceptedContentType_unsupportedType() throws Throwable {
		HttpURLConnection con = HttpClient.get("http://localhost:8088/my-test-serialized", "");
		con.setRequestProperty("Accept", "text/xml");

		StringBuffer content = HttpClient.readContent(con);
		final Map<String, List<String>> headers = HttpClient.readHeaders(con);

		assertTrue(headers.containsKey("Content-type"));
		assertEquals(headers.get("Content-type").get(0), "application/json");

		assertEquals("{\"string\":\"Simple string\"}", content.toString());
	}

	@Test
	public void checkedExceptionThrown_toServletFilter() throws Throwable {
		HttpURLConnection con = HttpClient.get("http://localhost:8088/checked-exception", "");
		String actual = HttpClient.readContent(con).toString();
		assertEquals("java.lang.Exception: My exception", actual);
	}

	@Test
	public void uncheckedExceptionThrown_toServletFilter() throws Throwable {
		HttpURLConnection con = HttpClient.get("http://localhost:8088/unchecked-exception", "");
		String actual = HttpClient.readContent(con).toString();
		assertEquals("java.lang.RuntimeException: My exception", actual);
	}

	@Test
	public void noSerializerCustomContentType_responseHelper() throws Throwable{
		HttpURLConnection con = HttpClient.get("http://localhost:8088/no-serializer-custom-content-type-response-helper", "");
		con.setRequestProperty("Accept", "application/xml");
		String actual = HttpClient.readContent(con).toString();
		assertEquals("<my-xml>woo-hoo</my-xml>", actual);
	}

	@Test
	public void noSerializerCustomContentType_annotation() throws Throwable{
		HttpURLConnection con = HttpClient.get("http://localhost:8088/no-serializer-custom-content-type-annotation", "");
		con.setRequestProperty("Accept", "application/xml");
		String actual = HttpClient.readContent(con).toString();
		assertEquals("<my-xml>woo-hoo</my-xml>", actual);
	}

	@Test
	public void noSerializerCustomContentType_unhappy() throws Throwable{
		HttpURLConnection con = HttpClient.get("http://localhost:8088/no-serializer-custom-content-type-response-helper", "");
		con.setRequestProperty("Accept", "my/xml");
		Map<String, List<String>> actual = HttpClient.readHeaders(con);

		assertEquals("HTTP/1.1 415 Unsupported Media Type", actual.get(null).get(0));

		con = HttpClient.get("http://localhost:8088/no-serializer-custom-content-type-annotation", "");
		con.setRequestProperty("Accept", "my/xml");
		actual = HttpClient.readHeaders(con);

		assertEquals("HTTP/1.1 415 Unsupported Media Type", actual.get(null).get(0));
	}
}