package com.bus.chelaile.innob.net;

import com.alibaba.fastjson.JSON;
import com.bus.chelaile.innob.request.*;


/**
 * Created by Administrator on 2016/8/8.
 */

public class JsonPayloadBuilder {
    public static String getRequestPayloadJson(Request request) {
        // JSONSerializableSerializer
        return JSON.toJSONString(request);
    }
}
