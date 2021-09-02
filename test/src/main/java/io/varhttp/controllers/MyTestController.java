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
	public String defaultValue(@RequestParameter(name = "param", defaultValue = "muh") String param) {
		return param;
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
			@RequestParameter(name = "date") Date date
	) {
		return date.toInstant().toString()+"-"+zonedDateTime.toInstant().toString();
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
}
