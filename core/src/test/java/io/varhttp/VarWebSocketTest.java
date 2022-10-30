package io.varhttp;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.junit.Assert.*;

public class VarWebSocketTest {
//    @Test
//    public void happyPathReceive() {
//        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[]{(byte) 129, (byte) 134, (byte) 167, (byte) 225, (byte) 225, (byte) 210, (byte) 198, (byte) 131, (byte) 130, (byte) 182, (byte) 194, (byte) 135});
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        AtomicReference<String> ref = new AtomicReference<>();
//        Consumer<String> consumer = ref::set;
//        VarWebSocket webSocket = new VarWebSocket(new WebSocketHelper(), inputStream, outputStream, consumer);
//        webSocket.handleNext();
//        assertEquals("abcdef", ref.get());
//    }
//
//    @Test
//    public void happyPathSend() {
//        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[]{});
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        AtomicReference<String> ref = new AtomicReference<>();
//        Consumer<String> consumer = ref::set;
//        VarWebSocket webSocket = new VarWebSocket(new WebSocketHelper(), inputStream, outputStream, consumer);
//        webSocket.send("abcdef");
//        webSocket.handleNext();
//        byte[] expectedBytes = new byte[]{(byte) 129, (byte) 134, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f'};
//
//        byte[] actualBytes = outputStream.toByteArray();
//
//        assertEquals(expectedBytes.length, actualBytes.length);
//
//        assertEquals("abcdef", new String(actualBytes).trim().substring(6));
//        for(int i=0;i<expectedBytes.length;i++) {
//            assertEquals("byte " +i,expectedBytes[i], actualBytes[i]);
//        }
//    }

}
