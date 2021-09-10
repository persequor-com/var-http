package io.varhttp.controllers;

import io.varhttp.Controller;
import io.varhttp.ControllerClass;
import io.varhttp.HttpMethod;
import io.varhttp.PathVariable;
import io.varhttp.RequestBody;
import io.varhttp.RequestHeader;
import io.varhttp.RequestParameter;
import io.varhttp.RequestParameters;
import io.varhttp.ResponseHeader;
import io.varhttp.ResponseStream;

import java.io.IOException;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@ControllerClass
public class MyTestController {
	@Controller(path = "/my-test")
	public String myTest() {
		return "Simple string";
	}

	@Controller(path = "/my-test-serialized")
	public TestResponse myTestSerialized() {
		return new TestResponse("Simple string");
	}

	@Controller(path = "/pathVar/{pathVar}")
	public String myTestPathVar(@PathVariable(name = "pathVar") String pathVar) {
		return pathVar;
	}

	@Controller(path = "/pathVar/{pathVar1}/{pathVar2}/{pathVar3}")
	public String myTestPathVarMultiLevel(@PathVariable(name = "pathVar1") String pathVar1, @PathVariable(name = "pathVar2") String pathVar2, @PathVariable(name = "pathVar3") String pathVar3) {
		return pathVar1+"-"+pathVar2+"-"+pathVar3;
	}

	@Controller(path = "/requestParameter")
	public String myTestRequestParameter(@RequestParameter(name = "var") String var) {
		return var;
	}

	@Controller(path = "/header")
	public String header(ResponseHeader responseHeader, RequestHeader requestHeader) {
		responseHeader.addHeader("My", requestHeader.getHeader("My"));
		responseHeader.setContentType("text/plainish");
		return "muh";
	}

	@Controller(path = "/defaultValue")
	public String defaultValue(@RequestParameter(name = "param", defaultValue = "muh", required = false) String param, @RequestParameter(name = "param2", defaultValue = "muh", required = false) String param2) {
		return param+"-"+param2;
	}

	@Controller(path = "/optionalBody")
	public String optionalBody(@RequestBody() Optional<String> param) {
		return param.orElse("Nothing passed in");
	}

	@Controller(path = "/primitives")
	public String primitives(
			@RequestParameter(name = "bool") boolean bool
			, @RequestParameter(name = "integer") int integer
			, @RequestParameter(name = "longer") long longer
			, @RequestParameter(name = "doubler") double doubler
			, @RequestParameter(name = "floater") float floater
	) {
		return String.valueOf(bool)+":"+String.valueOf(integer)+":"+String.valueOf(longer)+":"+String.valueOf(doubler)+":"+String.valueOf(floater);
	}

	@Controller(path = "/primitivesBoxed")
	public String primitivesBoxed(
			@RequestParameter(name = "bool") Boolean bool
			, @RequestParameter(name = "integer") Integer integer
			, @RequestParameter(name = "longer") Long longer
			, @RequestParameter(name = "doubler") Double doubler
			, @RequestParameter(name = "floater") Float floater
	) {
		return String.valueOf(bool)+":"+String.valueOf(integer)+":"+String.valueOf(longer)+":"+String.valueOf(doubler)+":"+String.valueOf(floater);
	}

	@Controller(path = "/listController")
	public List<String> listController(@RequestParameter(name = "list") List<String> list) {
		return list;
	}

	@Controller(path = "/listObject", httpMethods = HttpMethod.POST)
	public List<SerializableObject> listObject(@RequestBody List<SerializableObject> list) {
		list.forEach(a -> a.setName(a.getName()+"!"));
		return list;
	}

	@Controller(path = "/")
	public String root() {
		return "Who am i";
	}

	private class TestResponse {
		private String string;

		public TestResponse(String string) {
			this.string = string;
		}

		public String getString() {
			return string;
		}
	}

	@Controller(path = "/requestParameters")
	public String requestParameters(
			RequestParameters requestParameters
	) {
		if (requestParameters.contains("where")) {
			requestParameters.remove("where");
		}
		return requestParameters.get("what")+" is "+requestParameters.get("where");
	}

	@Controller(path = "/dates")
	public String requestParameters(
			@RequestParameter(name = "zonedDateTime")  ZonedDateTime zonedDateTime,
			@RequestParameter(name = "date") Date date,
			@RequestParameter(name = "localDate") LocalDate localDate

	) {
		return date.toInstant().toString()+"-"+zonedDateTime.toInstant().toString()+"-"+localDate.toString();
	}

	@Controller(path = "/requestBodyString")
	public String requestBodyString(
			@RequestBody String string,
			@RequestParameter(name = "otherParameter") String otherParameter
	) {
		return string;
	}

	@Controller(path = "/responseStream_getOutputStream_contentType")
	public void responseStream_getOutputStream_contentType(ResponseHeader responseHeader, ResponseStream responseStream) throws IOException {
		responseStream.getOutputStream("text/test").write("tadaaa".getBytes());
	}

	@Controller(path = "/getOutputStream_addiionalContentType")
	public void getOutputStream_addiionalContentType(ResponseHeader responseHeader, ResponseStream responseStream) throws IOException {
		responseHeader.setContentType("text/other");
		responseStream.getOutputStream("text/test").write("tadaaa".getBytes());
	}

	@Controller(path = "/returnJavascriptString")
	public String returnJavascriptString(ResponseHeader responseHeader) throws IOException {
		responseHeader.setContentType("application/javascript");
		return "alert('hello darkness my old friend')";
	}

	@Controller(path = "/javascriptInResponseStream")
	public void javascriptInResponseStream(ResponseHeader responseHeader, ResponseStream responseStream) throws IOException {
		responseHeader.setContentType("application/javascript");
		responseStream.write("alert('hello darkness my old friend')");
	}

	@Controller(path = "/headController", httpMethods = HttpMethod.HEAD)
	public void headController(ResponseHeader responseHeader) throws IOException {
		responseHeader.setStatus(200);
	}

	@AltControllerAnnotation(urlPath = "/altControllerAnnotation")
	public String altControllerAnnotation() {
		return "Kilroy was here";
	}

	@Controller(path = "/enumParameter/{enum}", httpMethods = HttpMethod.GET)
	public String altControllerAnnotation(@PathVariable(name = "enum") MyEnum myEnum) {
		return myEnum.stringValue();
	}
}
