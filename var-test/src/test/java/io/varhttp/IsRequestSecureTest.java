package io.varhttp;

import io.odinjector.OdinJector;
import io.varhttp.test.HttpClient;
import io.varhttp.test.HttpResponse;
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

        HttpResponse response  = HttpClient.readResponse(con);

        assertEquals("true", response.getContent());
    }

    @Test
    public void requestSecure_xProtoRequest() throws IOException {
        createLauncher(varConfig);

        Map<String, String> headers = new HashMap<>();
        headers.put("X-Forwarded-Proto", "https");

        HttpURLConnection con = HttpClient.get("http://localhost:8088/is-secure", "");
        headers.forEach(con::setRequestProperty);

        HttpResponse response = HttpClient.readResponse(con);
        assertEquals("true", response.getContent());

        headers = new HashMap<>();
        headers.put("X-Forwarded-Proto", "http");

        con = HttpClient.get("http://localhost:8088/is-secure", "");
        headers.forEach(con::setRequestProperty);

        response = HttpClient.readResponse(con);
        assertEquals("false", response.getContent());
    }

    @Test
    public void requestSecure_default_behaviour() throws IOException {
        varConfig.forceRequestsSecure(false);
        createLauncher(varConfig);

        HttpURLConnection con = HttpClient.get("http://localhost:8088/is-secure", "");

        HttpResponse response = HttpClient.readResponse(con);

        assertEquals("false", response.getContent());
    }

    private void createLauncher(VarConfig varConfig) {
        OdinJector odinJector = OdinJector.create().addContext(new OdinContext(varConfig));
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
