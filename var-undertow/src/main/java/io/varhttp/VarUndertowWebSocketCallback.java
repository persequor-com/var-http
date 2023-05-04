package io.varhttp;

import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.core.*;
import io.undertow.websockets.spi.WebSocketHttpExchange;

import java.util.concurrent.CompletableFuture;


public class VarUndertowWebSocketCallback implements WebSocketConnectionCallback {


    private CompletableFuture<VarWebSocket> futureSocket = new CompletableFuture<>();

    @Override
    public void onConnect(WebSocketHttpExchange exchange, WebSocketChannel channel) {
        UndertowWebSocket webSocket = new UndertowWebSocket(channel);
        futureSocket.complete(webSocket);
        channel.getReceiveSetter().set(new AbstractReceiveListener() {
            @Override
            protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) {
                webSocket.receive(channel, message);
            }
        });

        channel.resumeReceives();
    }


    public CompletableFuture<VarWebSocket> getWebSocket() {
        return futureSocket;
    }
}
