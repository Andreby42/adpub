package com.bus.chelaile.model;

import java.util.Map;

import com.bus.chelaile.util.New;

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

    public void redayPlacementCache(Map<String, Map<Integer, String>> androidPlacementMap,
            Map<String, Map<Integer, String>> iosPlacementMap) {

        Map<String, Map<Integer, String>> temp = androidPlacementMap;
        if (platform != null && platform.equalsIgnoreCase("ios")) {
            temp = iosPlacementMap;
        }

        if (temp.containsKey(aid)) {
            temp.get(aid).put(displayType, placementId);
        } else {
            Map<Integer, String> playM = New.hashMap();
            playM.put(displayType, placementId);
            temp.put(aid, playM);
        }
    }

}
