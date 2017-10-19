package com.bus.chelaile.model.rule;

import com.bus.chelaile.util.GpsUtils;






public class Position {
    private double lng;
    private double lat;
    private int dist;
    private String name;
    
    private static double BJ_JD_DIFF_1KM = 0.01172507; // 北京1KM的经度差
    private static double SH_JD_DIFF_1KM = 0.01051051;
    private static double BJ_WD_DIFF_1KM = 0.00911162; // 北京1KM的纬度差
    private static double SH_WD_DIFF_1KM = 0.009070294;
    
    public Position() {
    }
    
    public Position(double lng, double lat) {
    	this.lng = lng;
    	this.lat = lat;
    }

    public Position(double lng, double lat, int dist) {
        this.lng = lng;
        this.lat = lat;
        this.dist = dist;
    }
    
    public Position(double lng, double lat, int dist, String name) {
    	this.lng = lng;
        this.lat = lat;
        this.dist = dist;
        this.name = name;
    }
    
    public boolean isMatch(double lng2, double lat2) {
        // 首先分别通过GPS的经度、纬度的差距来初略的过滤到距离太远的点(超过1KM), 这样能够减少计算量。
        if (Math.abs(lng - lng2) > BJ_JD_DIFF_1KM) {
          //  return false;
        }
        
        if (Math.abs(lat - lat2) > BJ_WD_DIFF_1KM) {
         //   return false;
        }
        
        double currDist = GpsUtils.distance(lng, lat, lng2, lat2);
        if (currDist * 1000 > dist) {
            return false;
        }
        
        return true;
    }
    
    
    
    
    public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public int getDist() {
		return dist;
	}

	public void setDist(int dist) {
		this.dist = dist;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static void main(String[] args) {
    
      double lng = 	116.3688182616;
      double lat = 39.9927821321;
      Position st = new Position(lng,lat,3000);
      long time = System.currentTimeMillis();
      boolean br = st.isMatch(116.4037, 39.994914);
      time = System.currentTimeMillis() - time;
      
	}
    
}
