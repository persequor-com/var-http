package io.varhttp;

import java.lang.reflect.Method;

public interface ControllerListener {
    void onAdd(String path, Method method);
}
