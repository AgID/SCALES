package net.scales.dispatcher.utils;

import java.util.List;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class HttpUtils {

    public static int post(String url, List<NameValuePair> params, String jwtToken) throws Exception {
		HttpPost request = new HttpPost(url);

		if (jwtToken != null && !jwtToken.isEmpty()) {
			request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken);
		}

		request.setEntity(new UrlEncodedFormEntity(params));

		// Makes request
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpResponse response = httpClient.execute(request);

		return response.getStatusLine().getStatusCode();
	}

	public static String get(String url, String jwtToken) throws Exception {
		HttpGet request = new HttpGet(url);

		if (jwtToken != null && !jwtToken.isEmpty()) {
			request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken);
		}

		// Makes request
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpResponse response = httpClient.execute(request);

		return EntityUtils.toString(response.getEntity());
    }

}