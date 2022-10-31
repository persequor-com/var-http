package io.varhttp;

import io.odinjector.OdinJector;
import io.varhttp.test.VarClient;
import io.varhttp.test.VarClientHttp;
import io.varhttp.test.VarClientResponse;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import static org.junit.Assert.assertEquals;

public class VarWebSocketIT  {
    protected static VarClient varClient;
    protected static WebSocketHelper helper = new WebSocketHelper();

    private static LauncherUndertow launcher;

    @BeforeClass
    public static void setup() {
        OdinJector odinJector = OdinJector.create().addContext(new OdinContext(new VarConfig().setPort(8088))).addContext(new UndertowContext());
        launcher = odinJector.getInstance(LauncherUndertow.class);
        launcher.run();

        VarClientHttp varClientHttp = odinJector.getInstance(VarClientHttp.class);
        varClientHttp.withServerUrl("http://localhost:8088");
        varClient = varClientHttp;
    }


    @AfterClass
    public static void teardown() {
        launcher.stop();
    }

    @Test
    public void happyPath() throws Throwable {

        WsClient wsClient = new WsClient(new URI("http://localhost:8088/api/socketInit"));


    }

    public static class WsClient extends WebSocketClient {

        public WsClient(URI serverUri) {
            super(serverUri);
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            assertEquals(101, handshakedata.getHttpStatus());
            send("kilroy");
        }

        @Override
        public void onMessage(String message) {
            assertEquals("kilroy was here", message);
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {

        }

        @Override
        public void onError(Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
