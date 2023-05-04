package io.varhttp;

import java.io.IOException;
import java.util.function.Consumer;

public interface VarWebSocket {
    void send(VarWebSocketMessage message);
    void onReceive(Consumer<VarWebSocketMessage> message);
    void close() throws IOException;
}
