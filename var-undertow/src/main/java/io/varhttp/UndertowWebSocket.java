package io.varhttp;

import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;
import io.undertow.websockets.spi.WebSocketHttpExchange;

import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.function.Consumer;

public class UndertowWebSocket implements VarWebSocket {
    private final WebSocketHttpExchange exchange;
    private final WebSocketChannel channel;
    private Consumer<VarWebSocketMessage> messageConsumer;

    public UndertowWebSocket(WebSocketHttpExchange exchange, WebSocketChannel channel) {
        this.exchange = exchange;
        this.channel = channel;
    }

    public void receive(WebSocketChannel channel, BufferedTextMessage message) {
        if(messageConsumer == null) {
            System.out.println("no cinsumer: " + System.identityHashCode(this));
        }
        messageConsumer.accept(new VarWebSocketMessage(channel.getDestinationAddress().toString(), message.getData()));
    }

    @Override
    public void send(VarWebSocketMessage message) {
        System.out.println("send "+message.getData());
        ByteBuffer byteBuffer = ByteBuffer.wrap(message.getData().getBytes());
        //exchange.sendData(byteBuffer);

        WebSockets.sendText(message.getData(), channel, null);

    }

    @Override
    public void onReceive(Consumer<VarWebSocketMessage> messageConsumer) {
        System.out.println("on receive: "+System.identityHashCode(this));
        this.messageConsumer = messageConsumer;
    }

    @Override
    public String getPath() {
        return exchange.getRequestURI();
    }


    @Override
    public void close() {
        exchange.close();
    }

    @Override
    public void setSendingQueue(Queue<VarWebSocketMessage> messageConsumer) {

    }
}
