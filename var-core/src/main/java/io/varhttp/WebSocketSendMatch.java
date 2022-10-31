package io.varhttp;

import java.lang.reflect.Method;
import java.util.HashSet;

public class WebSocketSendMatch extends ControllerMatch {
    public WebSocketSendMatch(Method method) {
        super(method, null, new HashSet<>(), null);
    }
}
