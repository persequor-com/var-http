package io.varhttp;

public interface BiSocketReceiver<T, X> {
    void receive(T t, X x);
}
