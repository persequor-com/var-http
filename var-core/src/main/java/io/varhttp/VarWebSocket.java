package io.varhttp;

import java.util.Queue;
import java.util.function.Consumer;

public interface VarWebSocket {
    void send(VarWebSocketMessage message);
    void onReceive(Consumer<VarWebSocketMessage> message);

    String getPath();

    void close();

    void setSendingQueue(Queue<VarWebSocketMessage> messageConsumer);
}
