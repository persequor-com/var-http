package io.varhttp.parameterhandlers;

import io.varhttp.RegisteredWebSockets;
import io.varhttp.VarWebSocket;
import io.varhttp.WebSocket;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Singleton
public class VarWebSocketHandler implements IParameterHandlerMatcher {
    private RegisteredWebSockets registeredWebSockets;

    @Inject
    public VarWebSocketHandler(RegisteredWebSockets registeredWebSockets) {
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

            return (ctx) -> registeredWebSockets.get(webSocketAnnotation.path());
        }
        return null;
    }
}
