package io.varhttp;

public class UndertowWebsocketProvider implements IWebSocketProvider {
    @Override
    public Class<?> getWebsocketFilterClass() {
        return UndertowWebSocketVarFilter.class;
    }
}
