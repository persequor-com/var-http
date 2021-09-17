package io.varhttp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CORSHandlerTest {
    public static final String HTTP_METHOD_OPTIONS = "OPTIONS";
    public static final String HTTP_METHOD_GET = "GET";
    public static final int HTTP_STATUS_CODE = 405;

    public static final String HTTP_HOST = "localhost:3000";
    public static final String HTTP_ORIGIN = "http://" + HTTP_HOST;
    public static final String HTTP_DIFF_HOST = "localhost:8080";

    private CORSHandler corsHandler;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    @Before
    public void setup() throws URISyntaxException {
        when(request.getHeader("Origin")).thenReturn(HTTP_ORIGIN);
        when(request.getHeader("Host")).thenReturn(HTTP_DIFF_HOST);
        when(request.getMethod()).thenReturn(HTTP_METHOD_OPTIONS);
    }

    @Test
    public void happyPath_options() throws URISyntaxException {
        corsHandler= new CORSHandler(new CORSConfig());

        Set<HttpMethod> allowedMethods = new HashSet<>(Collections.singletonList(HttpMethod.GET));
        corsHandler.setHeaders(request, response, allowedMethods);

        verify(response, times(1)).addHeader("Access-Control-Allow-Credentials", "true");
        verify(response, times(1)).addHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        verify(response, times(1)).addHeader("Access-Control-Allow-Methods", "GET");
        verify(response, times(1)).addHeader("Access-Control-Allow-Headers", "content-type,x-requested-with");
        verify(response, times(1)).addHeader("Access-Control-Max-Age", "60");
    }

    @Test
    public void happyPath_get() throws URISyntaxException {
        corsHandler= new CORSHandler(new CORSConfig());
        when(request.getMethod()).thenReturn(HTTP_METHOD_GET);

        Set<HttpMethod> allowedMethods = new HashSet<>(Collections.singletonList(HttpMethod.GET));
        corsHandler.setHeaders(request, response, allowedMethods);

        verify(response, times(1)).addHeader("Access-Control-Allow-Credentials", "true");
        verify(response, times(1)).addHeader("Access-Control-Allow-Origin", HTTP_ORIGIN);
        verify(response, never()).addHeader("Access-Control-Allow-Methods", "GET");
        verify(response, never()).addHeader("Access-Control-Allow-Headers", "content-type,x-requested-with");
        verify(response, never()).addHeader("Access-Control-Max-Age", "60");
    }

    @Test
    public void noCors() throws URISyntaxException {
        corsHandler= new CORSHandler(new CORSConfig());
        when(request.getHeader("Host")).thenReturn(HTTP_HOST);
        Set<HttpMethod> allowedMethods = new HashSet<>(Collections.singletonList(HttpMethod.GET));
        corsHandler.setHeaders(request, response, allowedMethods);

        verify(response, never()).addHeader("Access-Control-Allow-Credentials", "true");
        verify(response, never()).addHeader("Access-Control-Allow-Origin", HTTP_ORIGIN);
        verify(response, never()).addHeader("Access-Control-Allow-Methods", "GET");
        verify(response, never()).addHeader("Access-Control-Allow-Headers", "content-type,x-requested-with");
        verify(response, never()).addHeader("Access-Control-Max-Age", "60");
    }

    @Test
    public void configuredAllowedOrigin_happyPath() throws URISyntaxException {
        corsHandler= new CORSHandler(new CORSConfig().allowedOrigins(HTTP_ORIGIN));

        Set<HttpMethod> allowedMethods = new HashSet<>(Collections.singletonList(HttpMethod.GET));
        corsHandler.setHeaders(request, response, allowedMethods);

        verify(response, times(1)).addHeader("Access-Control-Allow-Credentials", "true");
        verify(response, times(1)).addHeader("Access-Control-Allow-Origin", HTTP_ORIGIN);
        verify(response, times(1)).addHeader("Access-Control-Allow-Methods", "GET");
        verify(response, times(1)).addHeader("Access-Control-Allow-Headers", "content-type,x-requested-with");
        verify(response, times(1)).addHeader("Access-Control-Max-Age", "60");
        verify(response, never()).setStatus(HTTP_STATUS_CODE);
    }

    @Test
    public void configuredAllowedOrigin_rejectOrigin() throws URISyntaxException {
        corsHandler= new CORSHandler(new CORSConfig().allowedOrigins("http://localhost:5000"));

        Set<HttpMethod> allowedMethods = new HashSet<>(Collections.singletonList(HttpMethod.GET));
        corsHandler.setHeaders(request, response, allowedMethods);

        verify(response, never()).addHeader("Access-Control-Allow-Credentials", "true");
        verify(response, never()).addHeader("Access-Control-Allow-Origin", HTTP_ORIGIN);
        verify(response, never()).addHeader("Access-Control-Allow-Methods", "GET");
        verify(response, never()).addHeader("Access-Control-Allow-Headers", "content-type,x-requested-with");
        verify(response, never()).addHeader("Access-Control-Max-Age", "60");
        verify(response, times(1)).setStatus(HTTP_STATUS_CODE);
    }
}