package com.bus.chelaile.util;

import java.io.IOException;
import java.net.SocketTimeoutException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.bus.chelaile.util.config.PropertiesUtils;

public class HttpProxy {
	private static MultiThreadedHttpConnectionManager connManager = null;
	
	private static final Logger log = LoggerFactory
			.getLogger(HttpProxy.class);
	static
	{
		int connNum = Integer.parseInt(PropertiesUtils.getValue("public", "gaodeconnNum"));
		int expire = Integer.parseInt(PropertiesUtils.getValue("public", "gaodehttptimeout"));
		connManager = new MultiThreadedHttpConnectionManager();
		HttpConnectionManagerParams connMgrParams = connManager.getParams();
		connMgrParams.setMaxTotalConnections(connNum);
		connMgrParams.setDefaultMaxConnectionsPerHost(connNum);
		connMgrParams.setReceiveBufferSize(409600); // buffer size 400k.
		connMgrParams.setConnectionTimeout(expire);
		connMgrParams.setSoTimeout(expire);
		log.info("高德接口最大线程数:"+connNum+",超时时间:"+expire);
		log.info("高德接口初始化完成");
	}
	
	public static void init(){
		
	}
	
	 public static String requestGet(String url) {
	        

	        HttpClient httpClient = new HttpClient(connManager);
	        httpClient.getParams().setContentCharset("utf-8");
	        httpClient.getParams().setConnectionManagerTimeout(Integer.parseInt(PropertiesUtils.getValue("public", "connectManagerTimeout")));
	        httpClient.getParams().setSoTimeout(Integer.parseInt(PropertiesUtils.getValue("public", "gaodehttptimeout")));
	        GetMethod get = new GetMethod(url);
	        get.addRequestHeader("Connection", "Keep-Alive"); // 保持长链接
	    
	        int contentLen=-1;
	        String reqStatus= "OK";
	        long startTime = System.currentTimeMillis();
	        long endTime = 0;
	        try {
	            int httpCode = -1;
	            try {
	                httpCode = httpClient.executeMethod(get);
	            } catch (HttpException he) {
	                reqStatus = "HttpException(" + he.getLocalizedMessage() + ")";
	                log.error(String.format("调用接口异常 HttpException: url=%s", url), he);
	                return null;
	            } catch (SocketTimeoutException ste) {
	                reqStatus = "SocketTimeoutException(" + ste.getLocalizedMessage() + ")";
	                log.error(String.format("调用接口超时SocketTimeoutException: url=%s", url), ste);
	                return null;
	            } catch (IOException ioe) {
	                reqStatus = "IOException(" +ioe.getLocalizedMessage() + ")";
	                log.error(String.format("调用接口异常 IOException: url=%s", url), ioe);
	                return null;
	            }
	            endTime = System.currentTimeMillis();
	            if (httpCode >= 200 && httpCode < 300) {
	                String result = get.getResponseBodyAsString();
	                log.debug("调用URL返回结果： url={},  result={}", url, result);
	                if (result != null) {
	                	  contentLen = result.length();
	                    //result = new String(result.getBytes(get.getResponseCharSet()), "UTF-8");
	                }
	                return result;
	            } else {
	                reqStatus = "ERROR_HTTP_CODE#" + httpCode;
	                
	                log.error(String.format("调用接口错误: httpcode=%s, url=%s", httpCode, url));
	                return null;
	            }
	        } catch (Exception ex) {
	        	 endTime = System.currentTimeMillis();
	            reqStatus = "UnknownException(" + ex.getLocalizedMessage() + ")";
	            log.error("调用接口发生异常: url={}, errMsg={}", url, ex.getMessage());
	        } finally {
	            if (get != null) {
	                get.releaseConnection();
	            }

	            
//	            System.out.println(String.format("[REQUEST_URL_RESULT] 请求URL内容 |# time=%s |# status=%s |# contentLen=%s |# expire=%s |# conn=%s",
//	                    new Object[]{(endTime - startTime), reqStatus, contentLen, timeout, connName, url}));
	            log.info("[REQUEST_URL_RESULT] 请求URL内容 |# time={} |# status={} |# contentLen={} |# url={}", 
	                    new Object[]{(endTime - startTime), reqStatus, contentLen, url});
	        }

	        return null;
	    }
}
