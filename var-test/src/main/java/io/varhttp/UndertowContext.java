package io.varhttp;

import io.odinjector.Binder;
import io.odinjector.Context;
import io.undertow.websockets.WebSocketConnectionCallback;

public class UndertowContext extends Context {
    @Override
    public void configure(Binder binder) {
        binder.bind(WebSocketConnectionCallback.class).to(VarUndertowWebSocketHandler.class);
        binder.bind(IWebSocketProvider.class).to(UndertowWebsocketProvider.class);

    }
}
