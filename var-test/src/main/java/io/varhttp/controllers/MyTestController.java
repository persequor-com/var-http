package io.varhttp.controllers;

import io.varhttp.Controller;
import io.varhttp.ControllerClass;
import io.varhttp.Filter;
import io.varhttp.FilterMethod;
import io.varhttp.HttpMethod;
import io.varhttp.PathVariable;
import io.varhttp.RequestBody;
import io.varhttp.RequestHeader;
import io.varhttp.RequestParameter;
import io.varhttp.RequestParameters;
import io.varhttp.ResponseHeader;
import io.varhttp.ResponseStream;
import io.varhttp.VarFilterChain;
import io.varhttp.VarResponseStream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerClass
public class MyTestController {
	@Controller(path = "/my-test", httpMethods = {HttpMethod.GET})
	public String myTest() {
		return "Simple string";
	}

	@Controller(path = "/my-test-serialized", httpMethods = {HttpMethod.GET})
	public TestResponse myTestSerialized() {
		return new TestResponse("Simple string");
	}

	@Controller(path = "/pathVar/{pathVar}", httpMethods = {HttpMethod.GET})
	public String myTestPathVar(@PathVariable(name = "pathVar") String pathVar) {
		return pathVar;
	}

	@Controller(path = "/pathVar/{pathVar1}/{pathVar2}/{pathVar3}", httpMethods = {HttpMethod.GET})
	public String myTestPathVarMultiLevel(@PathVariable(name = "pathVar1") String pathVar1, @PathVariable(name = "pathVar2") String pathVar2, @PathVariable(name = "pathVar3") String pathVar3) {
		return pathVar1 + "-" + pathVar2 + "-" + pathVar3;
	}

	@Controller(path = "/requestParameter", httpMethods = {HttpMethod.GET})
	public String myTestRequestParameter(@RequestParameter(name = "var") String var, @RequestParameter(name = "datetime") ZonedDateTime date) {
		return var + " " + date;
	}

	@Controller(path = "/uuid/{uuid1}", httpMethods = {HttpMethod.GET})
	public String uuid(@PathVariable(name = "uuid1") UUID uuid1, @RequestParameter(name = "uuid2") UUID uuid2) {
		return uuid1 + " " + uuid2;
	}

	@Controller(path = "/required-request-params", httpMethods = {HttpMethod.GET})
	public String requiredRequestParams(@RequestParameter(name = "paramOne", required = true) Boolean paramOne, @RequestParameter(name = "paramTwo", required = true, defaultValue = "true") Boolean paramTwo) {
		return paramOne + "|" + paramTwo;
	}

	@Controller(path = "/header", httpMethods = {HttpMethod.GET})
	public String header(ResponseHeader responseHeader, RequestHeader requestHeader) {
		responseHeader.addHeader("My", requestHeader.getHeader("My"));
		responseHeader.setContentType("text/plainish");
		return "muh";
	}

	@Controller(path = "/header-path-info/*", httpMethods = {HttpMethod.GET})
	public String headerPathInfo(ResponseHeader responseHeader, RequestHeader requestHeader) {
		return requestHeader.getPath();
	}

	@Controller(path = "/http-servlet-request/*", httpMethods = {HttpMethod.GET})
	public String servletRequest(HttpServletRequest servletRequest) {
		return servletRequest.getRequestURI();
	}

	@Controller(path = "/http-servlet-request-not-implemented", httpMethods = {HttpMethod.GET})
	public void servletRequestAccessNotImplementedMethods(HttpServletRequest servletRequest) {
		servletRequest.getSession();
	}


	@Controller(path = "/defaultValue", httpMethods = {HttpMethod.GET})
	public String defaultValue(@RequestParameter(name = "param", defaultValue = "muh", required = false) String param, @RequestParameter(name = "param2", defaultValue = "muh", required = false) String param2) {
		return param + "-" + param2;
	}

	@Controller(path = "/optionalBody", httpMethods = {HttpMethod.POST})
	public String optionalBody(@RequestBody() Optional<String> param) {
		return param.orElse("Nothing passed in");
	}

	@Controller(path = "/primitives", httpMethods = {HttpMethod.POST})
	public String primitives(
			@RequestParameter(name = "bool") boolean bool
			, @RequestParameter(name = "integer") int integer
			, @RequestParameter(name = "longer") long longer
			, @RequestParameter(name = "doubler") double doubler
			, @RequestParameter(name = "floater") float floater
	) {
		return bool + ":" + integer + ":" + longer + ":" + doubler + ":" + floater;
	}

	@Controller(path = "/primitivesBoxed", httpMethods = {HttpMethod.POST})
	public String primitivesBoxed(
			@RequestParameter(name = "bool") Boolean bool
			, @RequestParameter(name = "integer") Integer integer
			, @RequestParameter(name = "longer") Long longer
			, @RequestParameter(name = "doubler") Double doubler
			, @RequestParameter(name = "floater") Float floater
	) {
		return bool + ":" + integer + ":" + longer + ":" + doubler + ":" + floater;
	}

	@Controller(path = "/listController", httpMethods = {HttpMethod.POST})
	public List<String> listController(@RequestParameter(name = "list") List<String> list) {
		return list;
	}

	@Controller(path = "/listObject", httpMethods = HttpMethod.POST)
	public List<SerializableObject> listObject(@RequestBody List<SerializableObject> list) {
		list.forEach(a -> a.setName(a.getName() + "!"));
		return list;
	}

	@Controller(path = "/", httpMethods = {HttpMethod.GET})
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

	@Controller(path = "/requestParameters", httpMethods = {HttpMethod.GET, HttpMethod.POST})
	public String requestParameters(
			RequestParameters requestParameters
	) {
		return requestParameters.get("what-õõ") + " is " + requestParameters.get("where");
	}

	@Controller(path = "/dates", httpMethods = {HttpMethod.POST})
	public String requestParameters(
			@RequestParameter(name = "zonedDateTime") ZonedDateTime zonedDateTime,
			@RequestParameter(name = "date") Date date,
			@RequestParameter(name = "localDate") LocalDate localDate

	) {
		return (date == null ? null : date.toInstant()) + "|" + (zonedDateTime == null ? null : zonedDateTime.toInstant()) + "|" + localDate;
	}

	@Controller(path = "/requestBodyString", httpMethods = {HttpMethod.POST})
	public String requestBodyString(
			@RequestBody String string,
			@RequestParameter(name = "otherParameter") String otherParameter
	) {
		return string;
	}

	@Controller(path = "/requestBodyInputStream", httpMethods = {HttpMethod.POST})
	public String requestBodyString(
			@RequestBody InputStream inputStream
	) {
		return new BufferedReader(
				new InputStreamReader(inputStream, StandardCharsets.UTF_8))
				.lines()
				.collect(Collectors.joining("\n"));
	}

	@Controller(path = "/responseStream_getOutputStream_contentType", httpMethods = {HttpMethod.POST})
	public void responseStream_getOutputStream_contentType(ResponseHeader responseHeader, ResponseStream responseStream) throws IOException {
		responseStream.getOutputStream("text/test").write("tadaaa".getBytes());
	}

	@Controller(path = "/getOutputStream_addiionalContentType", httpMethods = {HttpMethod.POST})
	public void getOutputStream_addiionalContentType(ResponseHeader responseHeader, ResponseStream responseStream) throws IOException {
		responseHeader.setContentType("text/other");
		responseStream.getOutputStream("text/test").write("tadaaa".getBytes());
	}

	@Controller(path = "/returnJavascriptString", httpMethods = {HttpMethod.POST})
	public String returnJavascriptString(ResponseHeader responseHeader) throws IOException {
		responseHeader.setContentType("application/javascript");
		return "alert('hello darkness my old friend')";
	}

	@Controller(path = "/javascriptInResponseStream", httpMethods = {HttpMethod.POST})
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

	@Controller(path = "/checked-exception", httpMethods = HttpMethod.GET)
	@Filter(ExceptionFilter.class)
	public String checkedException() throws Exception {
		throw new Exception("My exception");
	}

	@Controller(path = "/unchecked-exception", httpMethods = HttpMethod.GET)
	@Filter(ExceptionFilter.class)
	public String uncheckedException() throws Exception {
		throw new RuntimeException("My exception");
	}

	@Controller(path = "/checked-exception-var", httpMethods = HttpMethod.GET)
	@Filter(ExceptionVarFilter.class)
	public String checkedException_varFilter() throws Exception {
		throw new Exception("My exception");
	}

	@Controller(path = "/unchecked-exception-var", httpMethods = HttpMethod.GET)
	@Filter(ExceptionVarFilter.class)
	public String uncheckedException_varFilter() throws Exception {
		throw new RuntimeException("My exception");
	}

	@Controller(path = "/no-serializer-custom-content-type-response-helper", httpMethods = HttpMethod.GET)
	public String noSerializerCustomContentType_responseHelper(ResponseHeader responseHeader) throws Exception {
		responseHeader.setContentType("application/xml");
		return "<my-xml>woo-hoo</my-xml>";
	}

	@Controller(path = "/no-serializer-custom-content-type-annotation", httpMethods = HttpMethod.GET, contentType = "application/xml")
	public String noSerializerCustomContentType_annotation() throws Exception {
		return "<my-xml>woo-hoo</my-xml>";
	}

	public static class ExceptionVarFilter {
		@FilterMethod
		public void filter(VarFilterChain filterChain, VarResponseStream responseStream) {
			try {
				filterChain.proceed();
			} catch (Exception exception) {
				responseStream.write(exception.getClass().getName() + ": " + exception.getMessage());
			}
		}
	}

	public static class ExceptionFilter implements javax.servlet.Filter {
		@Override
		public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
			try {
				chain.doFilter(request, response);
			} catch (RuntimeException exception) {
				response.getWriter().print(exception.getClass().getName() + ": " + exception.getMessage());
				response.getWriter().flush();
			} catch (ServletException exception) {
				response.getWriter().print(exception.getCause().getClass().getName() + ": " + exception.getCause().getMessage());
				response.getWriter().flush();
			}
		}
	}


	@Controller(path = "/streamed-then-set-custom-http-code", httpMethods = {HttpMethod.GET})
	public void streamedOut_thenCustomCode(ResponseHeader responseHeader, ResponseStream responseStream) throws IOException {
		responseHeader.setStatus(HttpServletResponse.SC_ACCEPTED);
		try (OutputStream outputStream = responseStream.getOutputStream("text/test")) {
			outputStream.write("tadaaa".getBytes());
		}
		responseHeader.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
	}


	@Controller(path = "/streamed-then-failed", httpMethods = {HttpMethod.GET})
	public void streamedOut_thenFailed(ResponseHeader responseHeader, ResponseStream responseStream) throws IOException {
		responseHeader.setStatus(HttpServletResponse.SC_ACCEPTED);
		responseStream.getOutputStream("text/test").write("tadaaa".getBytes());
		throw new RuntimeException("should result in error code 500");
	}
}
