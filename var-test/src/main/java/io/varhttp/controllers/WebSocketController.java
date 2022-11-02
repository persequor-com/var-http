package io.varhttp.controllers;

import io.varhttp.*;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@ControllerClass
public class WebSocketController {
    private MyQueue myQueue;

    @Inject
    public WebSocketController(MyQueue myQueue) {
        this.myQueue = myQueue;
    }

    @WebSocket(path = "/api/socketInit")
    public void socket(RequestHeader requestHeader, VarWebSocket webSocket) {
        System.out.println("socket init");
        String cookie = requestHeader.getHeader("cookie");
        System.out.println("set on receive");
         webSocket.onReceive(message -> {
            System.out.println("was here");
            message.setData(message.getData()+" was here");
            webSocket.send(message);
        });
        webSocket.setSendingQueue(myQueue.getQueue(cookie));

    }

    @Controller(path = "/socketjs")
    public void js(ResponseStream response) {
        try {
            try(InputStream jsStream = WebSocketController.class.getResourceAsStream("/socket.html")) {
                transferTo(jsStream, response.getOutputStream("text/html"));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void transferTo(InputStream source, OutputStream target) throws IOException {
        byte[] buf = new byte[8192];
        int length;
        while ((length = source.read(buf)) != -1) {
            target.write(buf, 0, length);
        }
    }

}
