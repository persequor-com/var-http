package io.varhttp;

import io.undertow.servlet.spec.HttpServletRequestImpl;
import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.WebSocketProtocolHandshakeHandler;
import io.varhttp.parameterhandlers.IParameterHandler;
import io.varhttp.parameterhandlers.IParameterHandlerMatcher;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import static io.undertow.Handlers.websocket;

public class UndertowVarWebSocketHandler  implements IParameterHandlerMatcher {
    private RegisteredWebSockets registeredWebSockets;

    @Inject
    public UndertowVarWebSocketHandler(RegisteredWebSockets registeredWebSockets, WebSocketConnectionCallback callback) {
        this.registeredWebSockets = registeredWebSockets;
    }

    @Override
    public int getPriority() {
        return 10;
    }

    @Override
    public IParameterHandler getHandlerIfMatches(Method method, Parameter parameter, String path, String classPath) {
        if (VarWebSocket.class == parameter.getType()) {
            WebSocket webSocketAnnotation = method.getAnnotation(WebSocket.class);

            return (ctx) -> {
                return  registeredWebSockets.get(webSocketAnnotation.path());
            };
        }
        return null;
    }
}
