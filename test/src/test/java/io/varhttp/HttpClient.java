package io.varhttp;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.List;
import java.util.Map;

public class HttpClient {

	private static SSLContext sslContext;

	public static StringBuffer readContent(HttpURLConnection con) throws IOException {
		BufferedReader in = new BufferedReader(
				new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer content = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			content.append(inputLine);
		}
		in.close();
		con.disconnect();
		return content;
	}


	public static HttpURLConnection get(String urlString, String parameters) throws IOException {
		URL url = new URL(urlString+"?"+parameters);

		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		if (con instanceof HttpsURLConnection) {
			setTrustStore((HttpsURLConnection) con);
		}
		con.setRequestMethod("GET");
//		con.setDoOutput(true);
//		DataOutputStream out = new DataOutputStream(con.getOutputStream());
//		out.writeBytes(s1);
//		out.flush();
//		out.close();
		return con;
	}

	public static SSLContext getSslContext() {
		if (sslContext == null) {
			try {

				Certificate x509Certificate = CertificateFactory.getInstance("X.509").generateCertificate(Launcher.class.getResourceAsStream("/test.pem"));

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



	public static HttpURLConnection post(String path, String parameters) {
		return post(path, parameters, "application/x-www-form-urlencoded");
	}

	public static HttpURLConnection post(String path, String parameters, String contentType) {
		try {
			URL url = new URL(path);

			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			if (con instanceof HttpsURLConnection) {
				setTrustStore((HttpsURLConnection) con);
			}
			con.setRequestMethod("POST");
			if (parameters != null) {
				con.setRequestProperty( "Content-Type", contentType);

				con.setDoOutput(true);
				DataOutputStream out = new DataOutputStream(con.getOutputStream());
				out.writeBytes(parameters);
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
}
