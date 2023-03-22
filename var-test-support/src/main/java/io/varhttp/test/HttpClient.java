package io.varhttp.test;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class HttpClient {
	private static SSLContext sslContext;

	public static StringBuffer readContent(HttpURLConnection con) throws IOException {
		InputStream inputStream = getInputStream(con);
		if (inputStream == null) {
			return new StringBuffer();
		}
		BufferedReader in = new BufferedReader(
				new InputStreamReader(inputStream, StandardCharsets.UTF_8));
		String inputLine;
		StringBuffer content = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			content.append(inputLine);
		}
		in.close();
		con.disconnect();
		return content;
	}

	public static InputStream readDownloadableContent(HttpURLConnection con) throws IOException {
		InputStream inputStream = getInputStream(con);
		if (inputStream == null) {
			return new ByteArrayInputStream(new byte[]{});
		}
		return inputStream;
	}

	private static InputStream getInputStream(HttpURLConnection conn) throws IOException {
		int statusCode = conn.getResponseCode();
		if (statusCode >= 200 && statusCode < 400) {
			// Create an InputStream in order to extract the response object
			return conn.getInputStream();
		} else {
			return conn.getErrorStream();
		}
	}


	public static HttpURLConnection get(String urlString, String parameters) throws IOException {
		return getConnection(urlString + (parameters != null && !parameters.equals("") ? "?" + parameters : ""), "GET");
	}

	public static SSLContext getSslContext() {
		if (sslContext == null) {
			try {

				Certificate x509Certificate = CertificateFactory.getInstance("X.509").generateCertificate(HttpClient.class.getResourceAsStream("/test.pem"));

				KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
				ks.load(null, null);

				ks.setCertificateEntry("RSA", x509Certificate);


				// Set up the key manager factory
				KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
				kmf.init(ks, null);

				// Set up the trust manager factory
				TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
				tmf.init(ks);
				sslContext = SSLContext.getInstance("TLS");
				// Set up the HTTPS context and parameters
				sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return sslContext;
	}

	private static void setTrustStore(HttpsURLConnection con) {
		try {

			con.setSSLSocketFactory(getSslContext().getSocketFactory());

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	public static HttpURLConnection post(String path) {
		return post(path, null, conn -> {
		});
	}

	public static HttpURLConnection post(String path, String body) {
		return post(path, body, "application/x-www-form-urlencoded");
	}

	public static HttpURLConnection post(String path, String body, String contentType) {
		return post(path, body,
				connection -> connection.setRequestProperty("Content-Type", contentType));
	}

	public static HttpURLConnection post(String path, String body, Consumer<HttpURLConnection> consumer) {
		try {
			URL url = new URL(path);

			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			if (con instanceof HttpsURLConnection) {
				setTrustStore((HttpsURLConnection) con);
			}
			con.setRequestMethod("POST");
			if (body != null) {
				consumer.accept(con);
				con.setDoOutput(true);
				DataOutputStream out = new DataOutputStream(con.getOutputStream());
				out.writeBytes(body);
				out.flush();
				out.close();
			}
			return con;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Map<String, List<String>> readHeaders(HttpURLConnection con) {
		return con.getHeaderFields();
	}

	public static HttpURLConnection head(String urlString) throws IOException {
		return getConnection(urlString, "HEAD");
	}

	public static HttpURLConnection options(String urlString) throws IOException {
		return getConnection(urlString, "OPTIONS");
	}

	public static HttpURLConnection getConnection(String path, String method) throws IOException {
		URL url = new URL(path);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		if (con instanceof HttpsURLConnection) {
			setTrustStore((HttpsURLConnection) con);
		}
		con.setRequestMethod(method);
		return con;
	}

	public static HttpURLConnection delete(String urlString) throws IOException {
		return getConnection(urlString, "DELETE");
	}


	public static HttpURLConnection put(String urlString) throws IOException {
		return getConnection(urlString, "PUT");
	}
}
