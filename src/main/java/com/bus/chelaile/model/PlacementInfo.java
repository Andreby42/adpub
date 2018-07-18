package com.bus.chelaile.model;

import java.util.Map;

import lombok.Data;

@Data
public class PlacementInfo {

    private String pid;
    private String platform;
    private String aid;
    private int displayType;
    private String placementId;
    private String pidName;
    private String aidName;

    public void redayPlacementCache(Map<String, String> androidPlacementMap, Map<String, String> iosPlacementMap) {

        String key = pid + "_" + aid + "_" + displayType;
        String value = placementId;

        if (platform != null && platform.equalsIgnoreCase("ios")) {
            iosPlacementMap.put(key, value);
        } else {
            androidPlacementMap.put(key, value);
        }
    }

}
