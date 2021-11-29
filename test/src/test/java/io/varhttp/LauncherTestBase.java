package io.varhttp;

import io.varhttp.test.VarClient;
import org.junit.Test;

import java.util.UUID;

public class LauncherTestBase {

	protected static VarClient varClient;

	@Test
	public void simple() throws Throwable {
		varClient.get("/my-test")
				.execute()
				.contentType("text/plain")
				.content("Simple string");
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
				.contentType("text/plain");
	}

	@Test
	public void pathVariable() throws Throwable {
		varClient.get("/pathVar/my-string-%C3%B5%C3%B5")
				.execute()
				.content("my-string-õõ");
	}

	@Test
	public void pathVariableMultiple() throws Throwable {
		varClient.get("/pathVar/string1/string2/string3")
				.execute()
				.content("string1-string2-string3");
	}

	@Test
	public void requestParameter_stringAndDate() throws Throwable {
		varClient.get("/requestParameter?var=my&datetime=2017-06-16T21%3A51%3A30.211%2B05%3A30")
				.execute()
				.content("my 2017-06-16T21:51:30.211+05:30");
	}

	@Test
	public void requestParameter_uuid() throws Throwable {
		UUID uuid1 = UUID.randomUUID();
		UUID uuid2 = UUID.randomUUID();

		varClient.get("/uuid/" + uuid1)
				.parameter("uuid2", uuid2.toString())
				.execute()
				.content(uuid1 + " " + uuid2);
	}

	@Test
	public void serializedReturnObject() throws Throwable {
		varClient.get("/my-test-serialized").execute()
				.content("{\"string\":\"Simple string\"}");
	}

	@Test
	public void headers() throws Throwable {
		varClient.get("/header")
				.header("My", "Input header")
				.execute()
				.contentType("text/plainish")
				.header("My", "Input header")
				.content("muh");
	}

	@Test
	public void headerPathInfo() throws Throwable {
		varClient.get("/header-path-info/xxx?key=value")
				.execute()
				.content("/header-path-info/xxx");
	}

	@Test
	public void servletRequest() throws Throwable {
		varClient.get("/http-servlet-request/xxx?key=value")
				.execute()
				.content("/http-servlet-request/xxx");
	}

	@Test
	public void rootController() throws Throwable {
		varClient.get("/")
				.execute()
				.content("Who am i");
	}

	@Test
	public void prefixedController() throws Throwable {
		varClient.get("/packageprefix/classprefix/controller")
				.execute()
				.content("prefixed");
	}

	@Test
	public void defaultValueInParameter() throws Throwable {
		varClient.get("/defaultValue?param2=cat")
				.execute()
				.content("muh-cat");
	}

	@Test
	public void optionalBody() throws Throwable {
		varClient.post("/optionalBody")
				.execute()
				.content("Nothing passed in");
	}

	@Test
	public void primitiveParameters() throws Throwable {
		varClient.post("/primitives?bool=true&integer=43&longer=234423&doubler=0.4&floater=0.43")
				.execute()
				.content("true:43:234423:0.4:0.43");
	}

	@Test
	public void primitiveParameters_default() throws Throwable {
		varClient.post("/primitives")
				.execute()
				.content("false:0:0:0.0:0.0");
	}


	@Test
	public void primitivesBoxedParameters() throws Throwable {
		varClient.post("/primitivesBoxed?bool=true&integer=43&longer=234423&doubler=0.4&floater=0.43")
				.execute()
				.content("true:43:234423:0.4:0.43");
	}

	@Test
	public void primitivesBoxedParameters_default() throws Throwable {
		varClient.post("/primitivesBoxed")
				.execute()
				.content("null:null:null:null:null");
	}

	@Test
	public void redirectRelative() throws Throwable {
		varClient.post("/redirects/redirectRelative")
				.execute()
				.header("Location", "/redirects/target");
	}

	@Test
	public void redirect() throws Throwable {
		varClient.post("/redirects/redirect")
				.execute()
				.header("Location", "/redirects/target");
	}

	@Test
	public void redirectUrl() throws Throwable {
		varClient.post("/redirects/url")
				.execute()
				.header("Location", "http://github.com");
	}

	@Test
	public void requestParametersGet() throws Throwable {
		varClient.get("/requestParameters?what-%C3%B5%C3%B5%3DtheFuture-%C3%B5%C3%B5&where=here")
				.execute()
				.content("theFuture-õõ is null");
	}

	@Test
	public void requestParametersPost() throws Throwable {
		varClient.post("/requestParameters")
				.rawContent("what-%C3%B5%C3%B5%3DtheFuture-%C3%B5%C3%B5&where=here", "application/x-www-form-urlencoded")
				.execute()
				.content("theFuture-õõ is null");
	}

	@Test
	public void listController() throws Throwable {
		varClient.post("/listController?list=Muh&list=Miaw")
				.execute()
				.content("[\"Muh\",\"Miaw\"]");
	}

	@Test
	public void listObject() throws Throwable {
		varClient.post("/listObject")
				.rawContent("[{\"id\":\"id1\",\"name\":\"name1\"},{\"id\":\"id2\",\"name\":\"name2\"}]", "application/json")
				.execute()
				.content("[{\"id\":\"id1\",\"name\":\"name1!\"},{\"id\":\"id2\",\"name\":\"name2!\"}]");
	}

	@Test
	public void listController_listNotSet() throws Throwable {
		varClient.post("/listController")
				.execute()
				.content("");
	}

	@Test
	public void dates() throws Throwable {
		varClient.post("/dates?date=2020-01-01T12:30:15Z&zonedDateTime=2020-01-01T12:30:45Z&localDate=2020-01-10")
				.execute()
				.content("2020-01-01T12:30:15Z-2020-01-01T12:30:45Z-2020-01-10");
	}

	@Test
	public void requestBodyString() throws Throwable {
		varClient.post("/requestBodyString?otherParameter=param")
				.rawContent("This is a string, the only string my friend", "text/plain")
				.execute()
				.content("This is a string, the only string my friend");
	}

	@Test
	public void requestBodyInputStream() throws Throwable {
		varClient.post("/requestBodyInputStream")
				.rawContent("I AM THE BODY", "text/plain")
				.execute()
				.content("I AM THE BODY");
	}

	@Test
	public void responseStream_getOutputStream_contentType() throws Throwable {
		varClient.post("/responseStream_getOutputStream_contentType")
				.execute()
				.headerSize("Content-Type", 1)
				.contentType("text/test")
				.content("tadaaa");
	}

	@Test
	public void getOutputStream_addiionalContentType() throws Throwable {
		varClient.post("/getOutputStream_addiionalContentType")
				.execute()
				.headerSize("Content-Type", 1)
				.contentType("text/test")
				.content("tadaaa");
	}

	@Test
	public void returnJavascriptString() throws Throwable {
		varClient.post("/returnJavascriptString")
				.execute()
				.headerSize("Content-Type", 1)
				.contentType("application/javascript")
				.content("alert('hello darkness my old friend')");
	}

	@Test
	public void javascriptInResponseStream() throws Throwable {
		varClient.post("/javascriptInResponseStream")
				.execute()
				.headerSize("Content-Type", 1)
				.contentType("application/javascript")
				.content("alert('hello darkness my old friend')");}

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
				.content("Kilroy was here");
	}

	@Test
	public void enumParameter() throws Throwable {
		varClient.get("/enumParameter/Kilroy")
				.execute()
				.content("Kilroy was here");
	}

	@Test
	public void serializedReturnObject_toAcceptedContentType() throws Throwable {
		varClient.get("/my-test-serialized")
				.accept("application/xml")
				.execute()
				.contentType("application/xml")
				.content("<TestResponse><string>Simple string</string></TestResponse>");
	}

	@Test
	public void serializedReturnObject_toAcceptedContentType_applicationJson() throws Throwable {
		varClient.get("/my-test-serialized")
				.accept("application/json")
				.execute()
				.contentType("application/json")
				.content("{\"string\":\"Simple string\"}");
	}

	@Test
	public void serializedReturnObject_toAcceptedContentType_withNoAccept() throws Throwable {
		varClient.get("/my-test-serialized")
				.execute()
				.contentType("application/json")
				.content("{\"string\":\"Simple string\"}");
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
				.content("java.lang.Exception: My exception");
	}

	@Test
	public void uncheckedExceptionThrown_toServletFilter() throws Throwable {
		varClient.get("/unchecked-exception")
				.execute()
				.content("java.lang.RuntimeException: My exception");
	}

	@Test
	public void noSerializerCustomContentType_responseHelper() throws Throwable {
		varClient.get("/no-serializer-custom-content-type-response-helper")
				.accept("application/xml")
				.execute()
				.content("<my-xml>woo-hoo</my-xml>");
	}

	@Test
	public void noSerializerCustomContentType_annotation() throws Throwable {
		varClient.get("/no-serializer-custom-content-type-annotation")
				.accept("application/xml")
				.execute()
				.content("<my-xml>woo-hoo</my-xml>");
	}

	@Test
	public void noSerializerCustomContentType_unhappy() throws Throwable {
		varClient.get("/no-serializer-custom-content-type-response-helper")
				.accept("my/xml")
				.execute()
				.isUnsupportedMediaType();

		varClient.get("/no-serializer-custom-content-type-annotation")
				.accept("my/xml")
				.execute()
				.isUnsupportedMediaType();
	}
}