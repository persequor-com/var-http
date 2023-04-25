package io.varhttp;

import io.odinjector.OdinJector;
import io.varhttp.test.HttpClient;
import org.junit.After;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class IsRequestSecureTest {
    static Thread thread;
    private Launcher launcher;
    private final VarConfig varConfig = new VarConfig().setPort(8088);

    @After
    public void teardown() {
        if (launcher != null) {
            launcher.stop();
        }
    }

    @Test
    public void requestSecure_contextTrue() throws IOException {
        createLauncher(varConfig.forceRequestsSecure(true));

        HttpURLConnection con = HttpClient.get("http://localhost:8088/is-secure", "");

        StringBuffer content = HttpClient.readContent(con);

        assertEquals("true", content.toString());
    }

    @Test
    public void requestSecure_xProtoRequest() throws IOException {
        createLauncher(varConfig);

        Map<String, String> headers = new HashMap<>();
        headers.put("X-Forwarded-Proto", "https");

        HttpURLConnection con = HttpClient.get("http://localhost:8088/is-secure", "");
        headers.forEach(con::setRequestProperty);

        StringBuffer content = HttpClient.readContent(con);
        assertEquals("true", content.toString());

        headers = new HashMap<>();
        headers.put("X-Forwarded-Proto", "http");

        con = HttpClient.get("http://localhost:8088/is-secure", "");
        headers.forEach(con::setRequestProperty);

        content = HttpClient.readContent(con);
        assertEquals("false", content.toString());
    }

    @Test
    public void requestSecure_default_behaviour() throws IOException {
        varConfig.forceRequestsSecure(false);
        createLauncher(varConfig);

        HttpURLConnection con = HttpClient.get("http://localhost:8088/is-secure", "");

        StringBuffer content = HttpClient.readContent(con);

        assertEquals("false", content.toString());
    }

    private void createLauncher(VarConfig varConfig) {
        OdinJector odinJector = OdinJector.create().addContext(new OdinContext(varConfig))
                .addContext(new UndertowContext());
        launcher = odinJector.getInstance(Launcher.class);
        launcher.configure(config -> {
            config.addController(ContextControllerTest.class);
        });

        thread = new Thread(launcher);
        thread.run();
    }

    public static class ContextControllerTest {
        @Controller(path = "/is-secure")
        public boolean isSecure(HttpServletRequest request) {
            return request.isSecure();
        }
    }
}
