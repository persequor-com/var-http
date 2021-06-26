package io.varhttp;

import javax.inject.Singleton;

@Singleton
public class VarConfig {
	private int port = 80;

	public int getPort() {
		return port;
	}

	public VarConfig setPort(int port) {
		this.port = port;
		return this;
	}
}
