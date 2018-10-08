package com.bus.chelaile.third.IfengAx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.third.IfengAx.model.IfengRequestBody;
import com.bus.chelaile.third.IfengAx.model.response.IfengResponse;
import com.bus.chelaile.util.HttpUtils;

public class IfenAxService {

    private static final String URL_TEST = "http://axbox.deliver.ifeng.com/adx/bid?deal_id=3";
    private static final String URL_ONLINE = "https://iis1.deliver.ifeng.com/adx/bid?deal_id=3";

    private static final Logger logger = LoggerFactory.getLogger(IfenAxService.class);

    public static IfengResponse getContext(AdvParam p) {

        IfengResponse responseEntity = null;

        IfengRequestBody requestBody = new IfengRequestBody(p, 1, 2);
        System.out.println(JSONObject.toJSONString(requestBody));

            
            String result = HttpUtils.post(URL_TEST, JSONObject.toJSONString(requestBody));
            
            System.out.println(result);
            
//            String responseStr = inputStream.toString();
//            responseEntity = JSON.parseObject(responseStr, IfengResponse.class);
            

        return responseEntity;
    }

    void parseAdEntity() {

    }

    
    public static void main(String[] args) {
        AdvParam p = new AdvParam();
        p.setUdid("4ec14a47-6077-4e3d-87a2-cdece8d711f1");
//        p.setDpi("3");
        p.setIp("210.51.19.2");
        p.setS("android");
        p.setImei("847241048058157");
        p.setScreenHeight(1920);
        p.setScreenWidth(680);
        
        IfengResponse response = getContext(p);
        
        System.out.println(JSONObject.toJSONString(response));
        
        System.exit(1);
    } 
}
