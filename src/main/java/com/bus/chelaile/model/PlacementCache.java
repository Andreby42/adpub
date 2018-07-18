package com.bus.chelaile.model;

import java.util.Map;

import com.bus.chelaile.util.New;

import lombok.Data;

@Data
public class PlacementCache {
    
    private Map<String, Map<Integer, String>> info = New.hashMap();
    
    
}
