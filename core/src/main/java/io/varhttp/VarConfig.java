package io.varhttp;

import javax.inject.Singleton;
import java.util.Optional;

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

    private Boolean requestSecure;

    /**
     * @return  if the requests need to be force to secure.
	 * 
	 * @see #forceRequestsSecure(boolean)
     */
    public Boolean isForceRequestSecure() {
        return requestSecure;
    }


	/**
	 * <h2>Caution:
	 * <p>
	 * Forcing this will make your server requests behave either as secured(true) or insecure(false).
	 * <p>
	 * In other words this will affect the output of {@link VarHttpServletRequest#isSecure()}
	 *
	 * @return if server is secure.
	 */
    public VarConfig forceRequestsSecure(boolean serverSecure) {
        this.requestSecure = serverSecure;
        return this;
    }
}
