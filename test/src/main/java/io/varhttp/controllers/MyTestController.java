package io.varhttp.controllers;

import io.varhttp.Controller;
import io.varhttp.ControllerClass;
import io.varhttp.HttpMethod;
import io.varhttp.PathVariable;
import io.varhttp.RequestBody;
import io.varhttp.RequestHeader;
import io.varhttp.RequestParameter;
import io.varhttp.ResponseHeader;

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
}
