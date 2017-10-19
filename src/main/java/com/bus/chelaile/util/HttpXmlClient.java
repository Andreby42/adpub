package com.bus.chelaile.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class HttpXmlClient {
	private static Logger log = LoggerFactory.getLogger(HttpXmlClient.class);

	public static String post(String url, Map<String, String> params)
			throws ClientProtocolException, IOException {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		String body = null;

		log.info("create httppost:" + url);
		HttpPost post = postForm(url, params);

		body = invoke(httpclient, post);

		httpclient.getConnectionManager().shutdown();

		return body;
	}

	public static String get(String url) throws ClientProtocolException,
			IOException {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		String body = null;

		log.info("create httppost:" + url);
		HttpGet get = new HttpGet(url);
		body = invoke(httpclient, get);

		httpclient.getConnectionManager().shutdown();
		
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000); 
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 40000);

		return body;
	}

	private static String invoke(DefaultHttpClient httpclient,
			HttpUriRequest httpost) throws ClientProtocolException, IOException {

		HttpResponse response = sendRequest(httpclient, httpost);
		String body = paseResponse(response);

		return body;
	}

	private static String paseResponse(HttpResponse response)
			throws ParseException, IOException {
		log.info("get response from http server..");
		HttpEntity entity = response.getEntity();

	//	log.info("response status: " + response.getStatusLine());
		String charset = EntityUtils.getContentCharSet(entity);
	//	log.info(charset);

		String body = null;

		body = EntityUtils.toString(entity);

		return body;
	}

	private static HttpResponse sendRequest(DefaultHttpClient httpclient,
			HttpUriRequest httpost) throws ClientProtocolException, IOException {
		log.info("execute post...");
		HttpResponse response = null;

		response = httpclient.execute(httpost);

		return response;
	}

	private static HttpPost postForm(String url, Map<String, String> params)
			throws UnsupportedEncodingException {

		HttpPost httpost = new HttpPost(url);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();

		Set<String> keySet = params.keySet();
		for (String key : keySet) {
			nvps.add(new BasicNameValuePair(key, params.get(key)));
		}

		// log.info("set utf-8 form entity to httppost");
		httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

		return httpost;
	}

	public static void main(String[] args) throws ClientProtocolException, IOException {
		Map<String, String> params = New.hashMap();
//		params.put(
//				"tokenList",
//				"[{\"token\": \"70849e21114312bab5ce755e612e71722147544fd38c879856a1bfa579604cf5\",\"time\": 1437103677000}]");
		// params.put("password", password);

		String xml = HttpXmlClient.get("http://open.chelaile.net.cn:7000/bus/line!hasRealTimeData.action?cityId=004&sign=8rhIN87qUet4Hg8Dfe&v=3.0.0_20150814&s=android&lineId=0571-010-0");
		System.out.println(xml);
		// log.info(xml);
	}
}
