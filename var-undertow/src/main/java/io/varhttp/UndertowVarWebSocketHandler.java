package io.varhttp;

import io.undertow.websockets.WebSocketConnectionCallback;
import io.varhttp.RegisteredWebSockets;
import io.varhttp.parameterhandlers.IParameterHandler;
import io.varhttp.parameterhandlers.IParameterHandlerMatcher;

import javax.inject.Inject;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

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
