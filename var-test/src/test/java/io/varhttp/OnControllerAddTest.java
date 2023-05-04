package io.varhttp;

import io.odinjector.OdinJector;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class OnControllerAddTest {
	static Launcher launcher;
	static Thread thread;
	static Map<String, String> methods = new HashMap<>();

	@BeforeClass
	public static void setup() throws InterruptedException {
		OdinJector odinJector = OdinJector.create().addContext(new OdinContext(new VarConfig().setPort(8089)));
		launcher = odinJector.getInstance(Launcher.class);
		launcher.configure(config -> config.onControllerAdd((path, method) -> methods.put(path, method.getName())));
		thread = new Thread(launcher);
		thread.run();
	}

	@AfterClass
	public static void teardown() {
		launcher.stop();
	}

	@Test
	public void simple() throws Throwable {
		Set<String> expectedMethodNames =
				new TreeSet<>(Arrays.asList("login", "muh", "prefixed", "root", "defaultValue", "myTest",
						"myTestSerialized", "myTestPathVar", "myTestPathVarMultiLevel", "noSerializerCustomContentType_responseHelper",
						"myTestRequestParameter", "header", "headerPathInfo", "servletRequest", "socket", "optionalBody", "primitives",
						"primitivesBoxed", "listController", "listObject", "requestParameters", "requestParameters",
						"requestBodyString", "responseStream_getOutputStream_contentType", "getOutputStream_addiionalContentType",
						"returnJavascriptString", "noSerializerCustomContentType_annotation", "javascriptInResponseStream","js",
						"headController", "altControllerAnnotation", "altControllerAnnotation", "checkedException",
						"uncheckedException", "checkedException_varFilter", "uncheckedException_varFilter",
						"myTest", "redirect", "redirectRelative", "url", "contextdependent", "uuid"));
		Set<String> expectedPaths =
				new TreeSet<>(Arrays.asList("/header-path-info/*", "/requestParameter", "/headController", "/login",
						"/my-test-serialized", "/no-serializer-custom-content-type-response-helper",
						"/no-serializer-custom-content-type-annotation", "/contextdependent", "/controllable-endpoint", "/header", "/listObject",
						"/redirects/target", "/primitivesBoxed", "/my-test", "/redirects/redirect", "/dates",
						"/unchecked-exception", "/altControllerAnnotation", "/defaultValue", "/pathVar/{pathVar}",
						"/getOutputStream_addiionalContentType", "/listController", "/http-servlet-request/*",
						"/requestBodyString", "/checked-exception-var", "/primitives", "/checked-exception",
						"/muh", "/redirects/redirectRelative", "/enumParameter/{enum}", "/pathVar/{pathVar1}/{pathVar2}/{pathVar3}",
						"/anothercontextdependent", "/api/socketInit", "/", "/requestParameters", "/requestParameters", "/packageprefix/classprefix/controller",
						"/optionalBody", "/responseStream_getOutputStream_contentType", "/unchecked-exception-var",
						"/returnJavascriptString", "/javascriptInResponseStream", "/redirects/url",
						"/responseStream_getOutputStream_contentType", "/requestBodyInputStream", "/uuid/{uuid1}", "/socketjs"));

		Set<String> actualMethodNames = new TreeSet<>(methods.values());
		Set<String> actualPaths = new TreeSet<>(methods.keySet());

		assertEquals(expectedMethodNames.stream().collect(Collectors.joining("\n")), actualMethodNames.stream().collect(Collectors.joining("\n")));
		assertEquals(expectedPaths.stream().collect(Collectors.joining("\n")), actualPaths.stream().collect(Collectors.joining("\n")));
	}
}
