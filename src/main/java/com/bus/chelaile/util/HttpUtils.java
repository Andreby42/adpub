/**
 * Copyright ©2015 元光科技 All Rights Reserved
 */
package com.bus.chelaile.util;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.net.www.protocol.http.HttpURLConnection;

import com.alibaba.fastjson.util.IOUtils;
import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.tanxSSP.TanxProtos.Request;
import com.bus.chelaile.tanxSSP.TanxProtos.Response;
import com.bus.chelaile.util.config.PropertiesUtils;

/**
 * @author zzj 2015年4月25日
 * @mail zhijian.zhang@chelaile.net.cn
 */
public class HttpUtils {

	public static final PoolingHttpClientConnectionManager CM = new PoolingHttpClientConnectionManager();
	
	private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);
	
	private static final int connectTimeout = Integer.parseInt(PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			"connectTimeout", "30"));;
	private static final int connectionRequestTimeout = Integer.parseInt(PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			"connectionRequestTimeout", "30"));;
	private static final int socketTimeout = Integer.parseInt(PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			"socketTimeout", "30"));;

	/**
	 * 初始 连接池
	 */
	static {
		CM.setMaxTotal(20000);
		CM.setDefaultMaxPerRoute(Integer.parseInt(PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "default.max.per.route", "500")));
		// 校验失效的链接
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				CM.closeExpiredConnections();
				CM.closeIdleConnections(30, TimeUnit.SECONDS);
			}
		}, 0, 10 * 1000);
	}

	public static final RequestConfig REQUEST_CONFIG = RequestConfig.custom()
	// 建立链接超时
			.setConnectTimeout(1000 * connectTimeout)
			// 从conn mg 中获取链接超时
			.setConnectionRequestTimeout(connectionRequestTimeout * 1000)
			// 请求数据超时
			.setSocketTimeout(1000 * socketTimeout).build();

	public static final CloseableHttpClient HTTP_CLIENT = HttpClients.custom()
			.setConnectionManager(CM)
			.setKeepAliveStrategy(new ConnectionKeepAliveStrategy() {
				@Override
				public long getKeepAliveDuration(HttpResponse response,
						HttpContext context) {
					// Honor 'keep-alive' header
					HeaderElementIterator it = new BasicHeaderElementIterator(
							response.headerIterator(HTTP.CONN_KEEP_ALIVE));
					while (it.hasNext()) {
						HeaderElement he = it.nextElement();
						String param = he.getName();
						String value = he.getValue();
						System.out.printf("请求参数：%s，值：%s \n", param, value);
						if (value != null && param.equalsIgnoreCase("timeout")) {
							try {
								return Long.parseLong(value) * 1000;
							} catch (NumberFormatException ignore) {
							}
						}
					}
					return 20 * 1000;
				}
			}).setDefaultRequestConfig(REQUEST_CONFIG).build();

	public static String get(String url, List<NameValuePair> params,
			String encode) throws ParseException, UnsupportedEncodingException,
			IOException {
		String res = null;
		if (params != null && params.size() > 0) {
			url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(params));
		}
		logger.info("url={}", url);
		HttpGet get = new HttpGet(url);
		CloseableHttpResponse response = null;
		try {
			response = HTTP_CLIENT.execute(get, HttpClientContext.create());
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				res = EntityUtils.toString(entity, encode);
			}
		} finally {
			if (response != null) {
				response.close();
			}
		}
		return res;
	}
	
	/*
	 * NameValuePair形式的参数
	 */
	public static String post(String url, List<NameValuePair> params,
			String encode) throws ParseException, UnsupportedEncodingException,
			IOException {
		String res = null;
		if (params != null && params.size() > 0) {
			url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(params));
		}
		HttpPost  post = new HttpPost(url);
		CloseableHttpResponse response = null;
		try {
			response = HTTP_CLIENT.execute(post);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				res = EntityUtils.toString(entity, encode);
			}
		} finally {
			if (response != null) {
				response.close();
			}
		}
		return res;
	}
	
	/*
	 * post byte[]
	 */
	public static InputStream postBytes(String url, byte[] bytes, String contentType) throws ClientProtocolException, IOException {
		if(bytes == null || bytes.length == 0) {
			logger.error("post的bytes为空");
			return null;
		}
		HttpPost  post = new HttpPost(url);
		post.setEntity(new ByteArrayEntity(bytes));  
		InputStream in = null;
		if(contentType != null)
			post.setHeader("Content-type", contentType);
		CloseableHttpResponse response = null;
		try {
			Request r = Request.parseFrom(bytes);
			System.out.println("反序列化输入的对象：" + r.toString());
			
			response = HTTP_CLIENT.execute(post);
			in = response.getEntity().getContent();
			
//			HttpEntity entityResponse = response.getEntity();
//			int contentLength = (int) entityResponse.getContentLength();  
//			System.out.println("response内容大小： " + contentLength);
//            if (contentLength <= 0)  
//                throw new IOException("No response");  
//            byte[] respBuffer = new byte[contentLength];  
//            if (entityResponse.getContent().read(respBuffer) != respBuffer.length)  
//                throw new IOException("Read response buffer error");  
		} finally {
			if (response != null) {
				response.close();
			}
		}
		return in;
	}
	
	
	 public static String sendPost(String uri, byte[] bytes, String charset) {
	        String result = null;
	        DataOutputStream  out = null;
	        InputStream in = null;
	        try {
	            URL url = new URL(uri);
	            HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
	            urlcon.setDoInput(true);
	            urlcon.setDoOutput(true);
	            urlcon.setUseCaches(false);
	            urlcon.setRequestMethod("POST");
	            urlcon.setRequestProperty("Content-Type", "application/octet-stream");
	            urlcon.connect();// 获取连接
	            out = new DataOutputStream (urlcon.getOutputStream());
	            out.write(bytes);
	            out.flush();
	            in = urlcon.getInputStream();
	            
	            Response res = Response.parseFrom(in);
	            System.out.println(res.toString());
	            
	            BufferedReader buffer = new BufferedReader(new InputStreamReader(in, charset));
	            StringBuffer bs = new StringBuffer();
	            String line = null;
	            while ((line = buffer.readLine()) != null) {
	                bs.append(line);
	            }
	            result = bs.toString();
	        } catch (Exception e) {
	            System.out.println("[请求异常][地址：" + uri + "][参数：" + bytes + "][错误信息：" + e.getMessage() + "]");
	            e.printStackTrace();
	        } finally {
	            try {
	                if (null != in) in.close();
	                if (null != out) out.close();
	            } catch (Exception e2) {
	                System.out.println("[关闭流异常][错误信息：" + e2.getMessage() + "]");
	            }
	        }
	        return result;
	    }
	
	/**  
	  * 采取post方式提交序列化后的object对象 </br>  
	  * 另请参考：java.io.ObjectInputStream/ObjectOutputStream  
	  * @param requestUrl 请求地址  
	  * @param connTimeoutMills 设置连接主机超时，单位：毫秒  
	  * @param readTimeoutMills 设置从主机读取数据超时，单位：毫秒  
	  * @param serializedObject 序列化后的object对象  
	  *   
	  * @return remoteHttp返回的结果  
	  */    
	public static Response httpPostSerialObject(String requestUrl, Object serializedObject) throws Exception {
		Response responseData = null;

		HttpURLConnection httpUrlConn = null;
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		ObjectOutputStream oos = null;
		try {
			URL url = new URL(requestUrl);
			httpUrlConn = (HttpURLConnection) url.openConnection();
			// 设置content_type=SERIALIZED_OBJECT
			// 如果不设此项,在传送序列化对象时,当WEB服务默认的不是这种类型时可能抛java.io.EOFException
			httpUrlConn.setRequestProperty("Content-Type", "application/octet-stream");
//			httpUrlConn.setRequestProperty("Content-Type","application/x-java-serialized-object");
			httpUrlConn.setConnectTimeout(1000 * connectTimeout);
			httpUrlConn.setReadTimeout(1000 * connectTimeout);
			// 设置是否向httpUrlConn输出，因为是post请求，参数要放在http正文内，因此需要设为true, 默认情况下是false
			httpUrlConn.setDoOutput(true);
			// 设置是否从httpUrlConn读入，默认情况下是true
			httpUrlConn.setDoInput(true);
			// 不使用缓存
			httpUrlConn.setUseCaches(false);

			// 设置请求方式，默认是GET
			httpUrlConn.setRequestMethod("POST");
			httpUrlConn.connect();
//			System.out.println(JSONObject.toJSONString(httpUrlConn));

			if (serializedObject != null) {
				// 此处getOutputStream会隐含的进行connect，即：如同调用上面的connect()方法，
				// 所以在开发中不调用上述的connect()也可以，不过建议最好显式调用
				// write object(impl Serializable) using ObjectOutputStream
				oos = new ObjectOutputStream(httpUrlConn.getOutputStream());
				oos.writeObject(serializedObject);
				oos.flush();
//				System.out.println(httpUrlConn.toString());
				// outputStream不是一个网络流，充其量是个字符串流，往里面写入的东西不会立即发送到网络，
				// 而是存在于内存缓冲区中，待outputStream流关闭时，根据输入的内容生成http正文。所以这里的close是必须的
				oos.close();
//				System.out.println(httpUrlConn.toString());
			}

			inputStream = httpUrlConn.getInputStream();
//			ObjectInputStream inObj=new ObjectInputStream(inputStream);
//			responseData = (Response) inObj.readObject();
			responseData = Response.parseFrom(inputStream);

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				IOUtils.close(bufferedReader);
				IOUtils.close(inputStreamReader);
				IOUtils.close(inputStream);
				IOUtils.close(oos);
				if (httpUrlConn != null) {
					httpUrlConn.disconnect();
				}
			} catch (Exception e) {
			}
		}
		return responseData;
	}

	public static String get(String url, String encode)
			throws ClientProtocolException, IOException {
		String res = null;
		HttpGet get = new HttpGet(url);
		CloseableHttpResponse response = null;
		try {
//			get.setHeader("X-Forwarded-For", "111.111.111.1");
			response = HTTP_CLIENT.execute(get, HttpClientContext.create());
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				res = EntityUtils.toString(entity, encode);
			}
		} finally {
			if (response != null) {
				response.close();
			}
		}
		return res;
	}
	
	public static String getAndSetIp(String url, String encode, String x_forwarded_for)
			throws ClientProtocolException, IOException {
		String res = null;
		HttpGet get = new HttpGet(url);
		CloseableHttpResponse response = null;
		try {
			get.setHeader("X-Forwarded-For", x_forwarded_for);
			response = HTTP_CLIENT.execute(get, HttpClientContext.create());
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				res = EntityUtils.toString(entity, encode);
			}
		} finally {
			if (response != null) {
				response.close();
			}
		}
		return res;
	}
	
	public static String getUriAndSetIp(String url, List<NameValuePair> params,
			String encode, String x_forwarded_for) throws ParseException, UnsupportedEncodingException,
			IOException, URISyntaxException {
		String res = null;
		if (params != null && params.size() > 0) {
			url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(params));
		}
		System.out.println(url);
//		URL url1 = new URL(url);
//		URI uri = new URI(url1.getProtocol(), url1.getHost(), url1.getPath(), url1.getQuery(), null);
		HttpGet get = new HttpGet(url);
		CloseableHttpResponse response = null;
		try {
			get.setHeader("X-Forwarded-For", x_forwarded_for);
			response = HTTP_CLIENT.execute(get, HttpClientContext.create());
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				res = EntityUtils.toString(entity, encode);
			}
		} finally {
			if (response != null) {
				response.close();
			}
		}
		return res;
	}
	
	
	public static String getUri(String url, List<NameValuePair> params,
			String encode) throws ParseException, UnsupportedEncodingException,
			IOException, URISyntaxException {
		String res = null;
		if (params != null && params.size() > 0) {
			url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(params));
		}
		System.out.println(url);
//		URL url1 = new URL(url);
//		URI uri = new URI(url1.getProtocol(), url1.getHost(), url1.getPath(), url1.getQuery(), null);
		HttpGet get = new HttpGet(url);
		CloseableHttpResponse response = null;
		try {
			response = HTTP_CLIENT.execute(get, HttpClientContext.create());
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				res = EntityUtils.toString(entity, encode);
			}
		} finally {
			if (response != null) {
				response.close();
			}
		}
		return res;
	}
	

	public static void main(String[] args) throws ParseException,
			UnsupportedEncodingException, IOException {

//		String baiduUri = "http://api.map.baidu.com/geocoder/v2/?ak=a973f8b492f8c72de4fd099a318c17b56&location=39.136727,117.249471&output=json";
//		System.out.println(get(baiduUri, null, "utf-8"));
//		// 高德的几个key
//		// da8dd537807d127e3d41c74cbf185836
//		// eaa6713cc90f6a8f556c3f6ff1ca0542
//		// 8325164e247e15eea68b59e89200988b
//		System.out
//				.println(get(
//						"http://restapi.amap.com/v3/geocode/regeo?location=117.249471,39.136727&key=ada8dd537807d127e3d41c74cbf185836",
//						null, "utf-8"));
		
//		System.out.println(post("http://watsons.weixinyiwindow.com/activity/qrcode4in1/index.html?mch=kugou&unique_id=a354fd5bc9&sign=7c20fb89a7fee802dc8ff0edb4d72e14", null, "utf-8"));

		
		
		
		

	}
}
