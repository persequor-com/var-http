package io.varhttp;

import io.varhttp.test.VarClient;
import org.junit.Test;
import sun.misc.IOUtils;

import java.io.InputStream;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public abstract class LauncherTestBase {

	protected static VarClient varClient;

	@Test
	public void simple() throws Throwable {
		varClient.get("/my-test")
				.execute()
				.assertContentType("text/plain")
				.assertContent("Simple string");
	}

	@Test
	public void requestingUnsupportedContentType() throws Throwable {
		varClient.get("/my-test")
				.accept("my/custom")
				.execute()
				.isUnsupportedMediaType();
	}

	@Test
	public void notSettingAcceptHeaderContentType_willFallbackToServerDefault() throws Throwable {
		varClient.get("/my-test")
				.accept("")
				.execute()
				.assertContentType("text/plain");
	}

	@Test
	public void pathVariable() throws Throwable {
		varClient.get("/pathVar/my-string-%C3%B5%C3%B5")
				.execute()
				.assertContent("my-string-õõ");
	}

	@Test
	public void pathVariableMultiple() throws Throwable {
		varClient.get("/pathVar/string1/string2/string3")
				.execute()
				.assertContent("string1-string2-string3");
	}

	@Test
	public void requestParameter_stringAndDate() throws Throwable {
		varClient.get("/requestParameter?var=my&datetime=2017-06-16T21%3A51%3A30.211%2B05%3A30")
				.execute()
				.assertContent("my 2017-06-16T21:51:30.211+05:30");
	}

	@Test
	public void requestParameter_uuid() throws Throwable {
		UUID uuid1 = UUID.randomUUID();
		UUID uuid2 = UUID.randomUUID();

		varClient.get("/uuid/" + uuid1)
				.parameter("uuid2", uuid2.toString())
				.execute()
				.assertContent(uuid1 + " " + uuid2);
	}

	@Test
	public void serializedReturnObject() throws Throwable {
		varClient.get("/my-test-serialized").execute()
				.assertContent("{\"string\":\"Simple string\"}");
	}

	@Test
	public void headers() throws Throwable {
		varClient.get("/header")
				.header("My", "Input header")
				.execute()
				.assertContentType("text/plainish")
				.assertHeader("My", "Input header")
				.assertContent("muh");
	}

	@Test
	public void headerPathInfo() throws Throwable {
		varClient.get("/header-path-info/xxx?key=value")
				.execute()
				.assertContent("/header-path-info/xxx");
	}

	@Test
	public void servletRequest() throws Throwable {
		varClient.get("/http-servlet-request/xxx?key=value")
				.execute()
				.assertContent("/http-servlet-request/xxx");
	}

	@Test
	public void rootController() throws Throwable {
		varClient.get("/")
				.execute()
				.assertContent("Who am i");
	}

	@Test
	public void prefixedController() throws Throwable {
		varClient.get("/packageprefix/classprefix/controller")
				.execute()
				.assertContent("prefixed");
	}

	@Test
	public void defaultValueInParameter() throws Throwable {
		varClient.get("/defaultValue?param2=cat")
				.execute()
				.assertContent("muh-cat");
	}

	@Test
	public void optionalBody() throws Throwable {
		varClient.post("/optionalBody")
				.execute()
				.assertContent("Nothing passed in");
	}

	@Test
	public void primitiveParameters() throws Throwable {
		varClient.post("/primitives?bool=true&integer=43&longer=234423&doubler=0.4&floater=0.43")
				.execute()
				.assertContent("true:43:234423:0.4:0.43");
	}

	@Test
	public void primitiveParameters_default() throws Throwable {
		varClient.post("/primitives")
				.execute()
				.assertContent("false:0:0:0.0:0.0");
	}

	@Test
	public void primitivesBoxedParameters() throws Throwable {
		varClient.post("/primitivesBoxed?bool=true&integer=43&longer=234423&doubler=0.4&floater=0.43")
				.execute()
				.assertContent("true:43:234423:0.4:0.43");
	}

	@Test
	public void primitivesBoxedParameters_default() throws Throwable {
		varClient.post("/primitivesBoxed")
				.execute()
				.assertContent("null:null:null:null:null");
	}

	@Test
	public void redirectRelative() throws Throwable {
		varClient.post("/redirects/redirectRelative")
				.execute()
				.assertHeader("Location", "/redirects/target");
	}

	@Test
	public void redirect() throws Throwable {
		varClient.post("/redirects/redirect")
				.execute()
				.assertHeader("Location", "/redirects/target");
	}

	@Test
	public void redirectUrl() throws Throwable {
		varClient.post("/redirects/url")
				.execute()
				.assertHeader("Location", "http://github.com");
	}

	@Test
	public void requestParametersGet() throws Throwable {
		varClient.get("/requestParameters?what-%C3%B5%C3%B5=theFuture-%C3%B5%C3%B5&where=here")
				.execute()
				.assertContent("theFuture-õõ is here");
	}

	@Test
	public void requestParametersPost() throws Throwable {
		varClient.post("/requestParameters")
				.content("what-%C3%B5%C3%B5=theFuture-%C3%B5%C3%B5&where=here", "application/x-www-form-urlencoded")
				.execute()
				.assertContent("theFuture-õõ is here");
	}

	@Test
	public void listController() throws Throwable {
		varClient.post("/listController?list=Muh&list=Miaw")
				.execute()
				.assertContent("[\"Muh\",\"Miaw\"]");
	}

	@Test
	public void listObject() throws Throwable {
		varClient.post("/listObject")
				.content("[{\"id\":\"id1\",\"name\":\"name1\"},{\"id\":\"id2\",\"name\":\"name2\"}]", "application/json")
				.execute()
				.assertContent("[{\"id\":\"id1\",\"name\":\"name1!\"},{\"id\":\"id2\",\"name\":\"name2!\"}]");
	}

	@Test
	public void listController_listNotSet() throws Throwable {
		varClient.post("/listController")
				.execute()
				.assertContent("");
	}

	@Test
	public void dates() throws Throwable {
		varClient.post("/dates?date=2020-01-01T12:30:15Z&zonedDateTime=2020-01-01T12:30:45Z&localDate=2020-01-10")
				.execute()
				.assertContent("2020-01-01T12:30:15Z-2020-01-01T12:30:45Z-2020-01-10");
	}

	@Test
	public void requestBodyString() throws Throwable {
		varClient.post("/requestBodyString?otherParameter=param")
				.content("This is a string, the only string my friend", "text/plain")
				.execute()
				.assertContent("This is a string, the only string my friend");
	}

	@Test
	public void requestBodyInputStream() throws Throwable {
		varClient.post("/requestBodyInputStream")
				.content("I AM THE BODY", "text/plain")
				.execute()
				.assertContent("I AM THE BODY");
	}

	@Test
	public void responseStream_getOutputStream_contentType() throws Throwable {
		varClient.post("/responseStream_getOutputStream_contentType")
				.execute()
				.assertHeaderSize("Content-Type", 1)
				.assertContentType("text/test")
				.assertContent("tadaaa");
	}

	@Test
	public void responseStream_getOutputStream_contentType_download() throws Throwable {
		InputStream downloaded = varClient.post("/responseStream_getOutputStream_contentType")
				.downloadable()
				.execute()
				.assertHeaderSize("Content-Type", 1)
				.assertContentType("text/test")
				.download();
		assertEquals("tadaaa", new String(IOUtils.readAllBytes(downloaded)));
	}

	@Test
	public void getOutputStream_addiionalContentType() throws Throwable {
		varClient.post("/getOutputStream_addiionalContentType")
				.execute()
				.assertHeaderSize("Content-Type", 1)
				.assertContentType("text/test")
				.assertContent("tadaaa");
	}

	@Test
	public void getOutputStream_addiionalContentType_useAssertionLambda() throws Throwable {
		varClient.post("/getOutputStream_addiionalContentType")
				.execute()
				.assertOnResponse(httpResponse -> {
					assertEquals(1, httpResponse.getHeaders().getAll("Content-Type").size());
					assertEquals("text/test", httpResponse.getHeaders().get("Content-Type"));
					assertEquals("tadaaa", httpResponse.getContent());
				});
	}

	@Test
	public void returnJavascriptString() throws Throwable {
		varClient.post("/returnJavascriptString")
				.execute()
				.assertHeaderSize("Content-Type", 1)
				.assertContentType("application/javascript")
				.assertContent("alert('hello darkness my old friend')");
	}

	@Test
	public void javascriptInResponseStream() throws Throwable {
		varClient.post("/javascriptInResponseStream")
				.execute()
				.assertHeaderSize("Content-Type", 1)
				.assertContentType("application/javascript")
				.assertContent("alert('hello darkness my old friend')");}

	@Test
	public void headController() throws Throwable {
		varClient.head("/headController")
				.execute()
				.isOk();
	}

	@Test
	public void altControllerAnnotation() throws Throwable {
		varClient.get("/altControllerAnnotation")
				.execute()
				.assertContent("Kilroy was here");
	}

	@Test
	public void enumParameter() throws Throwable {
		varClient.get("/enumParameter/Kilroy")
				.execute()
				.assertContent("Kilroy was here");
	}

	@Test
	public void serializedReturnObject_toAcceptedContentType() throws Throwable {
		varClient.get("/my-test-serialized")
				.accept("application/xml")
				.execute()
				.assertContentType("application/xml")
				.assertContent("<TestResponse><string>Simple string</string></TestResponse>");
	}

	@Test
	public void serializedReturnObject_toAcceptedContentType_applicationJson() throws Throwable {
		varClient.get("/my-test-serialized")
				.accept("application/json")
				.execute()
				.assertContentType("application/json")
				.assertContent("{\"string\":\"Simple string\"}");
	}

	@Test
	public void serializedReturnObject_toAcceptedContentType_withNoAccept() throws Throwable {
		varClient.get("/my-test-serialized")
				.execute()
				.assertContentType("application/json")
				.assertContent("{\"string\":\"Simple string\"}");
	}

	@Test
	public void serializedReturnObject_toAcceptedContentType_unsupportedType() throws Throwable {
		varClient.get("/my-test-serialized")
				.accept("text/xml")
				.execute()
				.isUnsupportedMediaType();
	}

	@Test
	public void checkedExceptionThrown_toServletFilter() throws Throwable {
		varClient.get("/checked-exception")
				.execute()
				.assertContent("java.lang.Exception: My exception");
	}

	@Test
	public void uncheckedExceptionThrown_toServletFilter() throws Throwable {
		varClient.get("/unchecked-exception")
				.execute()
				.assertContent("java.lang.RuntimeException: My exception");
	}

	@Test
	public void noSerializerCustomContentType_responseHelper() throws Throwable {
		varClient.get("/no-serializer-custom-content-type-response-helper")
				.accept("application/xml")
				.execute()
				.assertContent("<my-xml>woo-hoo</my-xml>");
	}

	@Test
	public void noSerializerCustomContentType_annotation() throws Throwable {
		varClient.get("/no-serializer-custom-content-type-annotation")
				.accept("application/xml")
				.execute()
				.assertContent("<my-xml>woo-hoo</my-xml>");
	}

	@Test
	public void noSerializerCustomContentType_unhappy() throws Throwable {
		String response = varClient.get("/no-serializer-custom-content-type-response-helper")
				.accept("my/xml")
				.execute()
				.isUnsupportedMediaType()
				.getContent();

		assertEquals("io.varhttp.ContentTypeException Requested Content-Type 'my/xml' is not supported", response);

		response = varClient.get("/no-serializer-custom-content-type-annotation")
				.accept("my/xml")
				.execute()
				.isUnsupportedMediaType()
				.getContent();
		assertEquals("io.varhttp.ContentTypeException Requested Content-Type 'my/xml' is not supported", response);
	}
}