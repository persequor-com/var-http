package io.varhttp;

import io.odinjector.OdinJector;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OnControllerAddTest {
    static Launcher launcher;
    static Thread thread;
    static Map<String,String> methods = new HashMap<>();

    @BeforeClass
    public static void setup() throws InterruptedException {
        OdinJector odinJector = OdinJector.create().addContext(new OdinContext(new VarConfig().setPort(8089)));
        launcher = odinJector.getInstance(Launcher.class);
        launcher.configure(config -> config.onControllerAdd((path,method) -> methods.put(path, method.getName())));
        thread = new Thread(launcher);
        thread.run();
    }

    @AfterClass
    public static void teardown() {
        launcher.stop();
    }

    @Test
    public void simple() throws Throwable {
        List<String> expectedMethodNames = Arrays.asList("login", "muh", "prefixed", "root", "defaultValue", "myTest", "myTestSerialized", "myTestPathVar", "myTestPathVarMultiLevel", "myTestRequestParameter", "header", "headerPathInfo", "servletRequest", "optionalBody", "primitives", "primitivesBoxed", "listController", "listObject", "requestParameters", "requestParameters", "requestBodyString", "responseStream_getOutputStream_contentType", "getOutputStream_addiionalContentType", "returnJavascriptString", "javascriptInResponseStream", "headController", "altControllerAnnotation", "altControllerAnnotation", "checkedException", "uncheckedException", "checkedException_varFilter", "uncheckedException_varFilter", "myTest", "redirect", "redirectRelative", "url", "contextdependent", "contextdependent");
        List<String> expectedPaths = Arrays.asList("/header-path-info/*", "/requestParameter", "/headController", "/login", "/my-test-serialized", "/contextdependent", "/header", "/listObject", "/redirects/target", "/primitivesBoxed", "/my-test", "/redirects/redirect", "/dates", "/unchecked-exception", "/altControllerAnnotation", "/defaultValue", "/pathVar/{pathVar}", "/getOutputStream_addiionalContentType", "/listController", "/http-servlet-request/*", "/requestBodyString", "/checked-exception-var", "/primitives", "/checked-exception", "/muh", "/redirects/redirectRelative", "/enumParameter/{enum}", "/pathVar/{pathVar1}/{pathVar2}/{pathVar3}", "/anothercontextdependent", "/", "/requestParameters", "/packageprefix/classprefix/controller", "/optionalBody", "/responseStream_getOutputStream_contentType", "/unchecked-exception-var", "/returnJavascriptString", "/javascriptInResponseStream", "/redirects/url");

        List<String> actualMethodNames = new ArrayList<>(methods.values());
        List<String> actualPaths = new ArrayList<>(methods.keySet());

        assertTrue( "Not actual is different from expected. Expected should be: " + actualMethodNames,expectedMethodNames.size() == actualMethodNames.size() && actualMethodNames.containsAll(expectedMethodNames) && expectedMethodNames.containsAll(actualMethodNames));
        assertTrue( "Not actual is different from expected. Expected should be: " + actualPaths,expectedPaths.size() == actualPaths.size() && actualPaths.containsAll(expectedPaths) && expectedPaths.containsAll(actualPaths));
    }
}
