package io.varhttp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.ByteBuffer;
import java.util.BitSet;

public class WebSocketHelper {
    public byte[] readFrame(InputStream inputStream) {
        try {
            byte[] minimalHeader = new byte[2];

            minimalHeader = inputStream.readNBytes(2);
//            byte[] minimalHeader = inputStream.readNBytes(2);
            if (minimalHeader.length == 0) {
                return new byte[0];
            }
            int fin = minimalHeader[0] & (0x01 << 0);
            int rsv1 = minimalHeader[0] & (0x01 << 1);
            int rsv2 = minimalHeader[0] & (0x01 << 2);
            int rss3 = minimalHeader[0] & (0x01 << 3);
            int opCode = minimalHeader[0] & (0x04 << 4);
            if (opCode < 1) {
                return new byte[0];
            }
            long length = minimalHeader[1] + 128;
            if (length > 125) {
                if (length == 126) {
                    byte[] bytes = inputStream.readNBytes(2);
                    length = ((bytes[2] & 0xff) << 8) | (bytes[3] & 0xff);
                    ;
                } else if (length == 127) {
                    ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
                    buffer.put(inputStream.readNBytes(8));
                    buffer.flip();//need flip
                    length = buffer.getLong();
                }
            }

            byte[] key = inputStream.readNBytes(4);
            byte[] encoded = inputStream.readNBytes((int) length);
            byte[] decoded = new byte[(int) length];
            for (int i = 0; i < encoded.length; i++) {
                decoded[i] = (byte) (encoded[i] ^ key[i & 0x3]);
            }
            return decoded;
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public void writeFrame(OutputStream response, String message) {
        try {
            if (message.length() <= 125) {
                byte minimalHeader = 0;
                BitSet bitSet = new BitSet();
                bitSet.set(0);
                bitSet.set(8);
                byte h1 = (byte) (bitSet.toByteArray()[0] - 128);
                byte h2 = (byte) (message.length() - 128);
                ByteBuffer buffer = ByteBuffer.allocate(message.length() + 6);
                buffer.put(h1);
                buffer.put(h2);
                buffer.put(new byte[]{0, 0, 0, 0});
                byte[] bytes = message.getBytes();

                buffer.put(bytes);
                response.write(buffer.array());
            } else {
                throw new RuntimeException("Only short messages for now please");
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
