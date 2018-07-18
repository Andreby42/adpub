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

        if (platform != null && platform.equalsIgnoreCase("ios")) {
            if (iosPlacementMap.containsKey(aid)) {
                iosPlacementMap.get(aid).put(displayType, placementId);
            } else {
                Map<Integer, String> playM = New.hashMap();
                playM.put(displayType, placementId);
                iosPlacementMap.put(aid, playM);
            }
        } else {

            if (androidPlacementMap.containsKey(aid)) {
                androidPlacementMap.get(aid).put(displayType, placementId);
            } else {
                Map<Integer, String> playM = New.hashMap();
                playM.put(displayType, placementId);
                androidPlacementMap.put(aid, playM);
            }
        }
    }

}
