package io.varhttp;

import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.core.*;
import io.undertow.websockets.spi.WebSocketHttpExchange;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;


@Singleton
public class VarUndertowWebSocketHandler implements WebSocketConnectionCallback {
    private RegisteredWebSockets webSockets;

    @Inject
    public VarUndertowWebSocketHandler(RegisteredWebSockets webSockets) {
        this.webSockets = webSockets;
    }

    @Override
    public void onConnect(WebSocketHttpExchange exchange, WebSocketChannel channel) {
        UndertowWebSocket webSocket = new UndertowWebSocket(exchange, channel);
        exchange.getRequestURI();
        webSockets.add(exchange.getRequestURI(), webSocket);
        channel.getReceiveSetter().set(new AbstractReceiveListener() {

            @Override
            protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) {
                System.out.println("on full text message");
                webSocket.receive(channel, message);

                //WebSockets.sendText(message.getData(), channel, null);
            }
        });

        System.out.println("consume receives done");
        channel.resumeReceives();
    }
}
