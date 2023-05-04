package io.varhttp;

import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;

import java.io.IOException;
import java.util.function.Consumer;

public class UndertowWebSocket implements VarWebSocket {
    private final WebSocketChannel channel;
    private Consumer<VarWebSocketMessage> messageConsumer;

    public UndertowWebSocket(WebSocketChannel channel) {
        this.channel = channel;
    }

    public void receive(WebSocketChannel channel, BufferedTextMessage message) {
        if(messageConsumer == null) {
            System.out.println("no consumer: " + System.identityHashCode(this));
        }
        messageConsumer.accept(new VarWebSocketMessage(message.getData()));
    }

    @Override
    public void send(VarWebSocketMessage message) {
        WebSockets.sendText(message.getData(), channel, null);
    }

    @Override
    public void onReceive(Consumer<VarWebSocketMessage> messageConsumer) {
        this.messageConsumer = messageConsumer;
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }
}
