package io.varhttp;

import java.lang.reflect.Method;

public interface ControllerAddedHandler {
    void onAdd(String path, Method method);
}
