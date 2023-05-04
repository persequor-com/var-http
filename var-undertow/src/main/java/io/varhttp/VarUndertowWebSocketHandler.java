package io.varhttp;

import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.core.*;
import io.undertow.websockets.spi.WebSocketHttpExchange;

import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton
public class VarUndertowWebSocketHandler implements WebSocketConnectionCallback {
    private RegisteredWebSockets webSockets;

    @Inject
    public VarUndertowWebSocketHandler(RegisteredWebSockets webSockets) {
        this.webSockets = webSockets;
    }

    @Override
    public void onConnect(WebSocketHttpExchange exchange, WebSocketChannel channel) {
        UndertowWebSocket webSocket = new UndertowWebSocket(channel);
        webSockets.add(exchange.getRequestURI(), webSocket);
        channel.getReceiveSetter().set(new AbstractReceiveListener() {

            @Override
            protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) {
                webSocket.receive(channel, message);
            }
        });

        channel.resumeReceives();
    }
}
