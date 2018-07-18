package com.bus.chelaile.mvc.utils;

import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.model.ads.entity.TaskEntity;
import com.bus.chelaile.service.StaticAds;
import com.bus.chelaile.util.New;

import ch.qos.logback.core.net.SyslogOutputStream;


public class JSFileHandle {

    private static final Logger logger = LoggerFactory.getLogger(JSFileHandle.class);
    
    
    public static String replaceJs(String platform, String splashJS, ShowType showType, TaskEntity tgs, String tag) {

        Map<String, String> map = null;
        if (tgs != null && tgs.getTaskGroups() != null && tgs.getTaskGroups().getMap() != null) {
            map = tgs.getTaskGroups().getMap();
        } else {
            map = New.hashMap();
        }
        for (Entry<String, String> entry : map.entrySet()) {
            String displayType = entry.getValue();
            String aid = entry.getKey();

            String placementId = getPlaceMentId(platform, showType, aid, displayType);
            String placementReplaceKey = "${" + aid + "_placementId}";
            String displayTypeReplaceKey = "\"${" + aid + "_displayType}\"";
            String aidReplaceKey = "${" + aid + "}";

            splashJS = splashJS.replace(placementReplaceKey, placementId);
            splashJS = splashJS.replace(displayTypeReplaceKey, displayType);
            splashJS = splashJS.replace(aidReplaceKey, displayType);
        }

        return splashJS;
    }
    
    

    // 新的获取placementId的方式
    private static String getPlaceMentId(String platform, ShowType showType, String aid, String displayType) {
        StringBuilder key = new StringBuilder(showType.getType()).append("_").append(aid).append("_").append(displayType);
        if (platform.equals("android")) {
            return StaticAds.androidPlacementMap.get(key.toString());
        } else {
            return StaticAds.iosPlacementMap.get(key.toString());
        }
    }
    
    public static void main(String[] args) {
        String a = "return \"${sdk_toutiao_displayType}\"";
        System.out.println(a.replace("'${sdk_toutiao_displayType}'", "1111"));
        
        String aid = "sdk_toutiao";
        String displayTypeReplaceKey = "\"${" + aid + "_displayType}\"";
        System.out.println(displayTypeReplaceKey);
        System.out.println(a.replace("\"${sdk_toutiao_displayType}\"", "1111"));
    }
}
