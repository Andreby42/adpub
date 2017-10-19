package com.bus.chelaile.alimama;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;





import com.alibaba.fastjson.JSON;
import com.bus.chelaile.alimama.response.AlimamaResponse;
import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.util.config.PropertiesUtils;



/**
 * Created by Administrator on 2016/9/27.
 */
public class AlimamaRequestManager {
	protected static final Logger logger = LoggerFactory.getLogger(AlimamaRequestManager.class);
 //   private String url = "http://afpapi.alimama.com/api";
    private static final String charset = StandardCharsets.UTF_8.name();
    private static final String alimamaUrl = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),"alimamaUrl","http://afpapi.alimama.com/api");
    private static final String alimamaAid = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),"alimamaAid","62176667");
    private static final String iosPackageName = "com.chelaile.lite";
    private static final String androidPackageName = "com.ygkj.chelaile.standard";
    
    private static final int timeout= Integer.parseInt(PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
    		"adv.third.party.alimama.timeout.connection", "400000"));
    
    private static final int readout= Integer.parseInt(PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
    		"adv.third.party.alimama.timeout.read", "400000"));
    
    
    public AlimamaResponse getAlimamaResponse(String net,String vc,String ip,String deviceType,String s,String sv,String idfa,String imei) throws Exception{
    	String os = "iOS";
    	String packageName = iosPackageName;
    	String queryNet = "wifi";
    	
    	
    	if(s.equalsIgnoreCase(Constants.ANDROIDNAME)){
    		packageName = androidPackageName;
    		os = "Android";
    	}
    	
    	
    	
//    	if( net != null ){
//    		if(  )
//    	}
    	
    	AlimamaRequest request = new AlimamaRequest(alimamaAid, "unknown", "", vc, packageName,
                ip, "1", deviceType, os, sv, "320x50", "1136x640", idfa, imei, null);
    	
    	long startTime = System.currentTimeMillis();
    	
    	String response = fetchAd(request);
    	
    	startTime = System.currentTimeMillis() - startTime;
    	
    	logger.debug("startTime="+startTime);
    	
    	logger.debug(response);
    	
    	return JSON.parseObject(response, AlimamaResponse.class);
    }
    

    private String buildRequestQuery(AlimamaRequest request) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder("");
        Map<String, String> necessaryParameters = request.getNecessaryFieldsValues();
        Map<String, String> optionalParameters = request.getOptionalFieldsValues();
        for (String necessaryFieldName : request.getNecessaryFields()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            String necessaryFieldValue = necessaryParameters.get(necessaryFieldName);
            if (necessaryFieldValue == null) {
                throw new IllegalArgumentException(String.format("Necessary field %s is null", necessaryFieldName));
            }
            sb.append(String.format("%s=%s", necessaryFieldName, URLEncoder.encode(necessaryFieldValue, charset)));
        }
        for (String optionalFieldName : request.getOptionalFields()) {
            String optionalFieldValue = optionalParameters.get(optionalFieldName);
            if (optionalFieldValue != null) {
                sb.append(String.format("&%s=%s", optionalFieldName, URLEncoder.encode(optionalFieldValue, charset)));
            }
        }
        return sb.toString();
    }

    private String fetchAd(AlimamaRequest request) throws Exception {
        String query = buildRequestQuery(request);
       // System.out.println(query);
        String requestURL = alimamaUrl + "?" + query;
        logger.debug(requestURL);
        HttpURLConnection connection = (HttpURLConnection)new URL(requestURL).openConnection();
        setConnectionParams(connection);
        
        connection.setRequestProperty("Accept-Charset", charset);
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream response = connection.getInputStream();
            return convertStreamToString(response);
        } else {
            throw new Exception("HTTP request not OK.");
        }
    }
    
    private static void setConnectionParams(HttpURLConnection connection) throws ProtocolException {
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setConnectTimeout(timeout);
        connection.setReadTimeout(readout);
        connection.setUseCaches(false);
        connection.setRequestProperty("content-type", "application/json");
    }

    private String convertStreamToString(InputStream is) throws Exception {
        String response = null;
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line;
            while ((line = br.readLine()) != null)
                sb.append(line).append("\n");
            response = sb.toString();
        } finally {
            closeResource(is);
        }
        return response;
    }

    private void closeResource(Closeable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        AlimamaRequest request = new AlimamaRequest(alimamaAid, "wifi", "", "10231", "3.18.1",
                "182.18.10.10", "1", "iPhone8,4", "iOS", "10.0.2", "00", "Chelaile", "116.409274,39.996489", 2, null,
                null, "Apple", 460, "320x50", "1136x640", null, "CC4667A2-88D0-47F6-B646-C2B6CDF42CA2", null, null);
        AlimamaRequestManager manager = new AlimamaRequestManager();
        try {
            String response = manager.fetchAd(request);
            System.out.println(response);
            AlimamaResponse object = JSON.parseObject(response, AlimamaResponse.class);
            object.print();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
