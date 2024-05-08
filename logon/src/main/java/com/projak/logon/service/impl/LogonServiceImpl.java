package com.projak.logon.service.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.projak.logon.service.LogonService;

@Service
public class LogonServiceImpl implements LogonService {

	@Autowired
	private Environment env;

	@Override
	public HttpServletResponse addCookies(String userid, String password, HttpServletResponse response) {

		disableSslVerification();
		try {
			String stringURL = env.getProperty("url") + "logon";
//					+ ".do/?userid="+userid+"&password="+password;

			byte[] out = ("userid="+userid+"&password="+password).getBytes(StandardCharsets.UTF_8);
			int length = out.length;
			URL url = new URL(stringURL);
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			con.setFixedLengthStreamingMode(length);
			con.setRequestMethod("POST");
			con.setConnectTimeout(5000);
			con.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			con.setReadTimeout(5000);
			con.setDoOutput(true);
			try(OutputStream os = con.getOutputStream()) {
			    os.write(out);
			}
			con.connect();
			Map<String, List<String>> headers = con.getHeaderFields();
			Iterator<String> itr = headers.keySet().iterator();
			while (itr.hasNext()) {
				String header = itr.next();
				if (header != null && header.equalsIgnoreCase("Set-Cookie")) {
					List<String> cookies = headers.get(header);
					for (String httpCookie : cookies) {
						System.out.println("Setting Cookies : " + httpCookie + "; SameSite=None");
						response.addHeader("Set-Cookie", httpCookie + "; SameSite=None");
					}
				}
			}
			IOUtils.copy(con.getInputStream(), response.getWriter(), "UTF-8");
			con.disconnect();
		} catch (Exception ex) {
			ex.printStackTrace();
			try {
				response.sendError(500);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Exception on Adding Cookies : " + ex.fillInStackTrace());
		}

		return response;
	}

	public HttpServletResponse removeCookies(HttpServletResponse response, HttpServletRequest request)
			throws IOException {
		disableSslVerification();
		try {
			String stringURL = env.getProperty("url") + "logoff";
			URL url = new URL(stringURL);
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			if(request.getHeader("Cookie") != null) {
				con.setRequestProperty("Cookie", request.getHeader("Cookie"));
			}
			con.setRequestMethod("GET");
			con.setConnectTimeout(5000);
			con.setReadTimeout(5000);
			con.connect();
			Map<String, List<String>> headers = con.getHeaderFields();
			Iterator<String> itr = headers.keySet().iterator();
			while (itr.hasNext()) {
				String header = itr.next();
				if (header != null && header.equalsIgnoreCase("Set-Cookie")) {
					List<String> cookies = headers.get(header);
					for (String httpCookie : cookies) {
						System.out.println("Setting Cookies : " + httpCookie + "; SameSite=None");
						response.addHeader("Set-Cookie", httpCookie + "; SameSite=None");
					}
				}
			}
			IOUtils.copy(con.getInputStream(), response.getWriter(), "UTF-8");
			con.disconnect();
		} catch (Exception ex) {
			ex.printStackTrace();
			try {
				response.sendError(500);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Exception on Adding Cookies : " + ex.fillInStackTrace());
		}

		return response;
	}

	

	private void disableSslVerification() {
		try {
			// Create a trust manager that does not validate certificate chains
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}

				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			} };

			// Install the all-trusting trust manager
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};

			// Install the all-trusting host verifier
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
	}

}
