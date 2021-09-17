package io.varhttp;

import javax.inject.Singleton;
import java.util.Arrays;
import java.util.List;

@Singleton
public class CORSConfig {
    private String allowedMethods = "*";
    private boolean allowCredentials = true;
    private String allowedOrigins = "*";
    private Long maxAge = 60L;
    private List<String> secFetchSites = Arrays.asList("same-origin", "same-site");
    private String allowedHeaders = "content-type,x-requested-with";

    public String getAllowedMethods() {
        return allowedMethods;
    }

    public CORSConfig allowedMethods(String allowedMethods) {
        this.allowedMethods = allowedMethods;
        return this;
    }

    public boolean isAllowCredentials() {
        return allowCredentials;
    }

    public CORSConfig allowCredentials(boolean allowCredentials) {
        this.allowCredentials = allowCredentials;
        return this;
    }

    public String getAllowedOrigins() {
        return allowedOrigins;
    }

    public CORSConfig allowedOrigins(String allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
        return this;
    }

    public Long getMaxAge() {
        return maxAge;
    }

    public CORSConfig maxAge(Long maxAge) {
        this.maxAge = maxAge;
        return this;
    }

    public List<String> getSecFetchSites() {
        return secFetchSites;
    }

    public String getAllowedHeaders() {
        return allowedHeaders;
    }

    public CORSConfig allowedHeaders(String allowedHeaders) {
        this.allowedHeaders = allowedHeaders;
        return this;
    }
}
