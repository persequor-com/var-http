package io.varhttp.controllers;

import io.varhttp.VarWebSocketMessage;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public class MyQueue {
    private Map<String, LinkedBlockingQueue<VarWebSocketMessage>> queues = Collections.synchronizedMap(new HashMap<>());

    public LinkedBlockingQueue<VarWebSocketMessage> getQueue(String user) {
        return queues.computeIfAbsent(user, u ->  new LinkedBlockingQueue<>());
    }
}
