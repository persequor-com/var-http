package io.varhttp;

import io.undertow.Handlers;
import io.undertow.servlet.spec.HttpServletRequestImpl;
import io.undertow.websockets.WebSocketProtocolHandshakeHandler;
import io.varhttp.parameterhandlers.IParameterHandler;
import io.varhttp.parameterhandlers.IParameterHandlerMatcher;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.concurrent.TimeUnit;

public class VarWebSocketHandler implements IParameterHandlerMatcher {
	@Override
	public int getPriority() {
		return 10;
	}

	@Override
	public IParameterHandler getHandlerIfMatches(Method method, Parameter parameter, String path, String classPath) {
		if (VarWebSocket.class == parameter.getType()) {

			return (ctx) -> {
				VarUndertowWebSocketCallback callback = new VarUndertowWebSocketCallback();
				WebSocketProtocolHandshakeHandler ws = Handlers.websocket(callback);

				HttpServletRequestImpl request = (HttpServletRequestImpl) ((VarHttpServletRequest) ctx.request()).getRequest();
				try {
					ws.handleRequest(request.getExchange());
					return callback.getWebSocket().get(5, TimeUnit.SECONDS);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			};
		}
		return null;
	}
}
