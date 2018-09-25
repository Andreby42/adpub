package com.bus.chelaile.third.IfengAx;

import java.io.IOException;
import java.io.InputStream;

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

    private static final String URL_TEST = "http://axbox.deliver.ifeng.com/adx/bid?deal_id=123";
    //private static final String URL_ONLINE = "https://iis1.deliver.ifeng.com/adx/bid?deal_id=${dealid}";

    private static final Logger logger = LoggerFactory.getLogger(IfenAxService.class);

    public static IfengResponse getContext(AdvParam p) {

        IfengResponse responseEntity = null;

        IfengRequestBody requestBody = new IfengRequestBody(p, 1, 1);
        System.out.println(JSONObject.toJSONString(requestBody));

        try {
            InputStream inputStream = HttpUtils.postBytes(URL_TEST, requestBody.toString().getBytes(), "application/json");
            String responseStr = inputStream.toString();
            responseEntity = JSON.parseObject(responseStr, IfengResponse.class);
            

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return responseEntity;
    }

    void parseAdEntity() {

    }

    
    public static void main(String[] args) {
        AdvParam p = new AdvParam();
        p.setDpi("3");
        IfengResponse response = getContext(p);
        
        System.out.println(JSONObject.toJSONString(response));
    } 
}
