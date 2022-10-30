package io.varhttp;

import javax.inject.Provider;
import javax.inject.Singleton;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class RegisteredWebSockets {
    Map<String, VarWebSocket> methodMap = Collections.synchronizedMap(new HashMap<>());

    public RegisteredWebSockets() {
        int i = 1;
    }

    public VarWebSocket get(String path) {
        return methodMap.get(path);
    }

    public VarWebSocket add(String classPath, VarWebSocket webSocket) {
        return methodMap.computeIfAbsent(classPath, (p) -> webSocket);
    }
}
