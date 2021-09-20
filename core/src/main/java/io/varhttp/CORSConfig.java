package io.varhttp;

/**
 * Configuration based on the specification of CORS.
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS">https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS</a>
 */
public class CORSConfig {
    private String allowedMethods = "*";
    private boolean allowCredentials = false;
    private String allowedOrigins = "*";
    private Long maxAge = 60L;
    private String allowedHeaders = "*";

    public String getAllowedMethods() {
        return allowedMethods;
    }

    /**
     * Sets the configuration for CORS allowed methods. This can be:
     * <ul>
     *     <li><strong>Default</strong>: Wildcard <strong>'*'</strong>.</li>
     *     <li>Specific methods like GET, POST or PUT.</li>
     * </ul>
     *
     * <Strong>Note: If allowed credentials is enabled, the allowed methods must be an explicit list of methods.</Strong>
     *
     * @param allowedMethods comma separated allowed methods. Example: <strong>"*"</strong> or <strong>"GET,POST,PUT"</strong>
     */
    public CORSConfig allowedMethods(String allowedMethods) {
        this.allowedMethods = allowedMethods;
        return this;
    }

    public boolean isAllowCredentials() {
        return allowCredentials;
    }

    /**
     * Sets the configuration for CORS credentials mode. Can either be set to enable(true) or disable(false). Default: <strong>false</strong>.
     * @param allowCredentials enable or disable credentials mode.
     */
    public CORSConfig allowCredentials(boolean allowCredentials) {
        this.allowCredentials = allowCredentials;
        return this;
    }

    public String getAllowedOrigins() {
        return allowedOrigins;
    }

    /**
     * Sets the configuration for CORS allowed origins. This can be:
     * <ul>
     *     <li><strong>Default:</strong> Wildcard <code>'*'</code>.</li>
     *     <li>Specific origins like <code>http://localhost:3000</code> or <code>http://var</code>.</li>
     * </ul>
     *
     * <Strong>Note: If allowed credentials is enabled, the allowed origins must be an explicit list of origins.</Strong>
     *
     * @param allowedOrigins comma separated allowed origins. Example: <code>"*"</code> or <code>"http://localhost:3000,http://var"</code>
     */
    public CORSConfig allowedOrigins(String allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
        return this;
    }

    public Long getMaxAge() {
        return maxAge;
    }

    /**
     * Sets the allowed cache time in seconds for the prefight request. Default: 60 seconds.
     *
     * @param maxAge allowed cache time in seconds.
     */
    public CORSConfig maxAge(Long maxAge) {
        this.maxAge = maxAge;
        return this;
    }

    public String getAllowedHeaders() {
        return allowedHeaders;
    }

    /**
     * Sets the configuration for CORS allowed headers. This can be:
     * <ul>
     *    <li><strong>Default:</strong> Wildcard <code>'*'</code>.</li>
     *     <li>Specific origins like <code>content-type</code> or <code>x-requested-with</code>.</li>
     * </ul>
     *
     * <Strong>Note: If allowed credentials is enabled, the allowed headers must be an explicit list of headers.</Strong>
     *
     * @param allowedHeaders comma separated allowed headers. Example: <code>"*"</code> or <code>"content-type,x-requested-with"</code>
     */
    public CORSConfig allowedHeaders(String allowedHeaders) {
        this.allowedHeaders = allowedHeaders;
        return this;
    }
}
