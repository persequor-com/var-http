package io.varhttp.controllers;

import io.varhttp.*;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@ControllerClass
public class WebSocketController {

	@WebSocket(path = "/api/socketInit")
	public void socket(VarWebSocket webSocket) {
		System.out.println("socket init");
		System.out.println("set on receive");
		webSocket.onReceive(message -> {
			message.setData("Pong! Was: `%s`".formatted(message.getData()));
			webSocket.send(message);
		});

		new Thread(() -> {
			for (int i = 0; i < 10; i++) {
				webSocket.send(new VarWebSocketMessage("Background server call nr %s/10!".formatted(i + 1)));
				sleep(2000);
			}
		}).start();
	}

	private static void sleep(long sleepTime) {
		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Controller(path = "/socketjs", httpMethods = {HttpMethod.GET})
	public void js(ResponseStream response) {
		try {
			try (InputStream jsStream = WebSocketController.class.getResourceAsStream("/socket.html")) {
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
