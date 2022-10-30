package io.varhttp;

import io.odinjector.OdinJector;
import io.varhttp.test.VarClient;
import io.varhttp.test.VarClientHttp;
import io.varhttp.test.VarClientResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;

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
        VarClientResponse response = varClient
                .webSocket("/api/socketInit")
                .header("Sec-WebSocket-Key", "7WdRDSTRc2boRfvbRnQakg==")
                .header("Sec-WebSocket-Version", "13")
                .header("Upgrade", "websocket")
                .header("Authorization", "My user")
                .execute();
        System.out.println(response.getContent());
        response.assertStatusCode(101);

        InputStream inputStream = response.getInputStream();
        OutputStream outputStream = response.getOutputStream();

        outputStream.write(new byte[]{(byte) 129, (byte) 134, (byte) 167, (byte) 225, (byte) 225, (byte) 210, (byte) 198, (byte) 131, (byte) 130, (byte) 182, (byte) 194, (byte) 135});
        outputStream.flush();
//        outputStream.write("\n\n".getBytes());

        byte[] frame = null;
        for(int i=0;i<10;i++) {


            if (inputStream.available() > 0) {
                frame = helper.readFrame(inputStream);
                String stringFrame = new String(frame);
                assertEquals("Welcome My user", stringFrame);
                break;
            }
            Thread.sleep(500);
        }
        if (frame == null) {
            throw new RuntimeException("no frame?");
        }


        if (inputStream.available() > 0) {
            frame = helper.readFrame(inputStream);
            String stringFrame = new String(frame);
            assertEquals("Welcome My user", stringFrame);
        } else {
            throw new RuntimeException("No frame?!");
        }

        int a = 0;
    }
}
