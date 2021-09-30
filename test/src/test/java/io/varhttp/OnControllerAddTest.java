package io.varhttp;

import io.odinjector.OdinJector;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OnControllerAddTest {
    static Launcher launcher;
    static Thread thread;
    static List<String> methods = new ArrayList<>();

    @BeforeClass
    public static void setup() throws InterruptedException {
        OdinJector odinJector = OdinJector.create().addContext(new OdinContext(new VarConfig().setPort(8089)));
        launcher = odinJector.getInstance(Launcher.class);
        launcher.configure(config -> config.onControllerAdd(method -> methods.add(method.getName())));
        thread = new Thread(launcher);
        thread.run();
    }

    @AfterClass
    public static void teardown() {
        launcher.stop();
    }

    @Test
    public void simple() throws Throwable {
        List<String> actual = Arrays.asList(
                "login",
                "muh",
                "prefixed",
                "root",
                "defaultValue",
                "myTest",
                "myTestSerialized",
                "myTestPathVar",
                "myTestPathVarMultiLevel",
                "myTestRequestParameter",
                "header",
                "headerPathInfo",
                "servletRequest",
                "optionalBody",
                "primitives",
                "primitivesBoxed",
                "listController",
                "listObject",
                "requestParameters",
                "requestParameters",
                "requestBodyString",
                "responseStream_getOutputStream_contentType",
                "getOutputStream_addiionalContentType",
                "returnJavascriptString",
                "javascriptInResponseStream",
                "headController",
                "altControllerAnnotation",
                "altControllerAnnotation",
                "checkedException",
                "uncheckedException",
                "checkedException_varFilter",
                "uncheckedException_varFilter",
                "myTest",
                "redirect",
                "redirectRelative",
                "url",
                "contextdependent",
                "contextdependent");
        assertTrue( "Not actual is different from expected. Expected should be: " + methods.toString(),actual.size() == methods.size() && methods.containsAll(actual) && actual.containsAll(methods));
    }
}
