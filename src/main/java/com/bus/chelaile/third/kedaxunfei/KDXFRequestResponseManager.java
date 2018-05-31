package com.bus.chelaile.third.kedaxunfei;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.bus.chelaile.innob.request.Request;

/**
 * Created by Administrator on 2016/8/9.
 */
public class KDXFRequestResponseManager {
    private static final String TERMINAL      = "http://ws.voiceads.cn/ad/request";
   
    private static final Logger logger = LoggerFactory
            .getLogger(KDXFRequestResponseManager.class);

    private static void setConnectionParams(HttpURLConnection connection) throws ProtocolException {
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setConnectTimeout(60 * 1000);
        connection.setReadTimeout(30 * 1000);
        connection.setUseCaches(false);
        //connection.setRequestProperty("content-type", "application/json");
        connection.setRequestProperty("X-protocol-ver", "2.0");
        connection.setRequestProperty("Accept-Encoding", "gzip");
    }

    private static void postData(HttpURLConnection connection, String payload) throws IOException {
        connection.setRequestProperty("content-length", Integer.toString(payload.length()));
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            bw.write(payload);
        } finally {
            closeResource(bw);
        }
    }
    
//    private String getResponseBodyAsString() throws IOException {  
//        GZIPInputStream gzin;  
//        if (getResponseBody() != null || getResponseStream() != null) {  
//              
//            if(getResponseHeader("Content-Encoding") != null  
//                     && getResponseHeader("Content-Encoding").getValue().toLowerCase().indexOf("gzip") > -1) {  
//                    //For GZip response  
//                    InputStream is = getResponseBodyAsStream();  
//                    gzin = new GZIPInputStream(is);  
//                      
//                    InputStreamReader isr = new InputStreamReader(gzin, getResponseCharSet());   
//                    java.io.BufferedReader br = new java.io.BufferedReader(isr);  
//                    StringBuffer sb = new StringBuffer();  
//                    String tempbf;  
//                    while ((tempbf = br.readLine()) != null) {  
//                        sb.append(tempbf);  
//                        sb.append("\r\n");  
//                    }  
//                    isr.close();  
//                    gzin.close();  
//                    return sb.toString();  
//                }  else {  
//                //For deflate response  
//                return super.getResponseBodyAsString();  
//            }  
//        } else {  
//            return null;  
//        }  
//    }  

    public static InputStream fetchAdResponseAsStream(String payloadStr) {
     //   String payloadStr = JsonPayloadBuilder.getRequestPayloadJson(request);
    	 GZIPInputStream gzin;
        InputStream is = null;
        if (payloadStr != null) {
            try {
                URL serverUrl = new URL(TERMINAL);
                HttpURLConnection connection = (HttpURLConnection)serverUrl.openConnection();
                setConnectionParams(connection);
                logger.debug(String.format("Post payload is %s", payloadStr));
                // System.out.println(payloadStr);
                postData(connection, payloadStr);
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    // System.out.println(connection.getResponseMessage());
                    // System.out.println(connection.get);
                    is = connection.getInputStream();
                } else {
                    logger.error("Request got an error code {}", connection.getResponseCode());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return is;
    }

    private static String convertStreamToString(InputStream is) throws IOException {
    	GZIPInputStream gzin = new GZIPInputStream(is);  
         
         InputStreamReader isr = new InputStreamReader(gzin, "UTF-8");   
         java.io.BufferedReader br = new java.io.BufferedReader(isr);  
         StringBuffer sb = new StringBuffer();  
         String tempbf;  
         while ((tempbf = br.readLine()) != null) {  
             sb.append(tempbf);  
             sb.append("\r\n");  
         }  
         isr.close();  
         gzin.close();  
         return sb.toString();  
//        String response = null;
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        StringBuilder sb = new StringBuilder();
//
//        try {
//            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
//            String line;
//            while ((line = br.readLine()) != null)
//                sb.append(line).append("\n");
//            response = sb.toString();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            closeResource(is);
//        }
//        return response;
    }

    public static String fetchAdResponseAsString(String context) {
        String response = null;
        try {
            InputStream is = fetchAdResponseAsStream(context);
            if (is == null)
                logger.error("Ad response is null");
            response = convertStreamToString(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    private static void closeResource(Closeable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
