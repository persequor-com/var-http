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

    private boolean requestSecure = false;

    /**
     * @return a boolean indicating if the requests need to be force to a secure channel.
     * @see #forceRequestsSecure(boolean)
     */
    public boolean isForceRequestSecure() {
        return requestSecure;
    }


    /**
     *
     * <p>
     * <u>Caution using this method</u>. Passing <code>true</code> to the method will make your server requests behave as secured.
     * In other words this will affect the output of {@link VarHttpServletRequest#isSecure()}
     * <p>
     * This can be useful if your server is behind a proxy/load balancer and is in a secure channel.
     * <p>
     * The default behaviour is <code>false</code>.
     *
     * @return updated {@link VarConfig} instance.
     */
    public VarConfig forceRequestsSecure(boolean serverSecure) {
        this.requestSecure = serverSecure;
        return this;
    }
}
