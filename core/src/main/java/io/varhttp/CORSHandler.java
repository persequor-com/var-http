package io.varhttp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


public class CORSHandler {
    private final CORSConfig config;

    public CORSHandler(CORSConfig config) {
        this.config = config;
    }

    public void setHeaders(HttpServletRequest request, HttpServletResponse response, Set<HttpMethod> allowedMethods) throws URISyntaxException {
        String originHeader = request.getHeader("Origin");
        String hostHeader = request.getHeader("Host");
        String requestMethod = request.getHeader("Access-Control-Request-Method");
        String requestHeaders = request.getHeader("Access-Control-Request-Headers");

        if (originHeader != null && hostHeader != null && !isSameOriginAndHost(originHeader, hostHeader)) {
            if (isAllowedOrigin(originHeader)) {
                if (config.isAllowCredentials()) {
                    response.addHeader("Access-Control-Allow-Credentials", String.valueOf(config.isAllowCredentials()));
                }

                response.addHeader("Access-Control-Allow-Origin", originHeader);

                if (request.getMethod().equals("OPTIONS")) {
                    response.addHeader("Access-Control-Allow-Methods", getAllowedMethods(allowedMethods, requestMethod));
                    response.addHeader("Access-Control-Allow-Headers", config.getAllowedHeaders());
                    response.addHeader("Access-Control-Max-Age", String.valueOf(config.getMaxAge()));
                }
            }
        }
    }

    private boolean isSameOriginAndHost(String originHeader, String hostHeader) throws URISyntaxException {
        URI origin = new URI(originHeader);

        final List<String> hostInfo = Arrays.asList(hostHeader.split(":"));

        return Objects.equals(origin.getHost(), hostInfo.get(0)) &&
                !(hostInfo.size() > 1 && !Objects.equals(String.valueOf(origin.getPort()), hostInfo.get(1)));
    }

    private String getAllowedMethods(Set<HttpMethod> allowedMethods, String requestMethod) {
        if (config.getAllowedMethods() != null && config.getAllowedMethods().equals("*")) {
            if(!allowedMethods.isEmpty()) {
                return String.join(",", allowedMethods.stream().map(Enum::toString).collect(Collectors.toSet()));
            } else {
                return requestMethod;
            }
        }

        return config.getAllowedMethods();
    }

    private boolean isAllowedOrigin(String requestOrigin) {
        final String allowedOrigins = config.getAllowedOrigins();
        return allowedOrigins.equals("*") ||
                Arrays.asList(allowedOrigins.split(",")).contains(requestOrigin);
    }
}
