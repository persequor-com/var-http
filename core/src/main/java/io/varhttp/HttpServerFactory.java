package io.varhttp;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;

import javax.inject.Inject;
import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.stream.Collectors;

public class HttpServerFactory {

    private final VarConfig varConfig;
    private SSLContext sslContext;

    @Inject
    public HttpServerFactory(VarConfig varConfig) {
        this.varConfig = varConfig;
    }

    public HttpServer getServer() {
        try {
            if (sslContext != null) {
                HttpsServer httpsServer = HttpsServer.create(new InetSocketAddress(varConfig.getPort()), 0);
                httpsServer.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
                    @Override
                    public void configure(HttpsParameters params) {
                        try {
                            // Initialise the SSL context
                            SSLContext c = SSLContext.getDefault();
                            SSLEngine engine = c.createSSLEngine();
                            params.setNeedClientAuth(false);
                            params.setCipherSuites(engine.getEnabledCipherSuites());

                            params.setProtocols(engine.getEnabledProtocols());

                            // Get the default parameters
                            SSLParameters defaultSSLParameters = c.getDefaultSSLParameters();
                            params.setSSLParameters(defaultSSLParameters);
                        } catch (NoSuchAlgorithmException httpsInitialisationException) {
                            throw new VarInitializationException(httpsInitialisationException);
                        }
                    }
                });
                return httpsServer;
            } else {
                return HttpServer.create(new InetSocketAddress(varConfig.getPort()), 0);
            }
        } catch (IOException exception) {
            throw new VarInitializationException(exception);
        }
    }

    public void setSslContext(SSLContext sslContext) {
        this.sslContext = sslContext;
    }

    public void setSslContext(KeyStore keyStore, char[] password) {
        try {
            // Set up the key manager factory
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(keyStore, password);

            // Set up the trust manager factory
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(keyStore);

            // Set up the HTTPS context and parameters
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        } catch (NoSuchAlgorithmException | KeyStoreException | UnrecoverableKeyException | KeyManagementException e) {
            throw new VarInitializationException(e);
        }
    }

    public void setSslContext(InputStream x509Certificate, InputStream privateKey) {
        try {
            sslContext = SSLContext.getInstance("TLS");

            // Initialise the keystore
            char[] password = "".toCharArray();
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(null, password);
            java.security.cert.Certificate certificate = CertificateFactory.getInstance("X.509")
                    .generateCertificate(x509Certificate);

            String text = new BufferedReader(
                    new InputStreamReader(privateKey, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));

            text = text.replaceAll("-----(BEGIN|END) PRIVATE KEY-----","").replace("\n","");
            text = text.replaceAll("","");
            byte[] dec = Base64.getDecoder().decode(text);

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(dec);

            PrivateKey privKey = KeyFactory.getInstance("RSA").generatePrivate(keySpec);


            ks.setCertificateEntry("server", certificate);
            ks.setKeyEntry("server", privKey,"".toCharArray(), new Certificate[]{certificate});


            setSslContext(ks, password);
            // Set up the key manager factory
        } catch (NoSuchAlgorithmException | KeyStoreException | CertificateException | IOException | InvalidKeySpecException e) {
            throw new VarInitializationException(e);
        }
    }
}
