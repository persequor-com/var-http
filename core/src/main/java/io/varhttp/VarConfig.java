package io.varhttp;

import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class VarConfig {
	private int port = 80;
	private Optional<Boolean> secureContext = Optional.empty();

	public int getPort() {
		return port;
	}

	public VarConfig setPort(int port) {
		this.port = port;
		return this;
	}

	public Optional<Boolean> getSecureContext() {
		return secureContext;
	}

	public VarConfig setSecureContext(boolean secureContext) {
		this.secureContext = Optional.of(secureContext);
		return this;
	}
}
