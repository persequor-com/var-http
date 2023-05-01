package io.varhttp;

import io.undertow.servlet.spec.HttpServletRequestImpl;
import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.WebSocketProtocolHandshakeHandler;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import static io.undertow.Handlers.websocket;

public class UndertowWebSocketVarFilter {
    private final WebSocketProtocolHandshakeHandler ws;

    @Inject
    public UndertowWebSocketVarFilter(WebSocketConnectionCallback callback) {
        ws = websocket(callback);

    }

    @FilterMethod
    public void undertowFilter(VarFilterChain filterChain, HttpServletRequest req) throws Exception {
        try {
            HttpServletRequestImpl request = (HttpServletRequestImpl) ( (VarHttpServletRequest)req).getRequest();
            ws.handleRequest(request.getExchange());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        filterChain.proceed();

    }
}
