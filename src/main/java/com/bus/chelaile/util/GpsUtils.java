package com.bus.chelaile.util;



public class GpsUtils {
	public static double geo_distance(double jingdu1,double weidu1,double jingdu2,double weidu2){
		if(jingdu1==jingdu2 && weidu1==weidu2 ) {  
            return 0.0;  
        } else {  
        	// earth's mean radius in KM  
            double r = 6378.137;  
            weidu1 = Math.toRadians(weidu1);  
            jingdu1 = Math.toRadians(jingdu1);  
            weidu2 = Math.toRadians(weidu2);  
            jingdu2 = Math.toRadians(jingdu2);  
            double d1 = Math.abs(weidu1 - weidu2);  
            double d2 = Math.abs(jingdu1 - jingdu2);  
            double p = Math.pow(Math.sin(d1 / 2), 2) + Math.cos(weidu1)  
                    * Math.cos(weidu2) * Math.pow(Math.sin(d2 / 2), 2);  
            double dis = r * 2 * Math.asin(Math.sqrt(p));  
            return dis;  
        }  
	}
	
	//将角度转换为弧度  
	public static double deg2rad(double degree) {  
	        return degree / 180 * Math.PI;  
	}  
	//将弧度转换为角度  
	public static double rad2deg(double radian) {  
	        return radian * 180 / Math.PI;  
	}  
	
	/**
	 * 计算两个GPS点直线距离
	 * 结果单位：KM
	 * @param jingdu1
	 * @param weidu1
	 * @param jingdu2
	 * @param weidu2
	 * @return
	 */
	public static double distance(double jingdu1,double weidu1,double jingdu2,double weidu2) {  
        double theta = jingdu1 - jingdu2;  
        double dist = Math.sin(deg2rad(weidu1)) * Math.sin(deg2rad(weidu2))  
                                + Math.cos(deg2rad(weidu1)) * Math.cos(deg2rad(weidu2))  
                                * Math.cos(deg2rad(theta));  
        dist = Math.acos(dist);  
        dist = rad2deg(dist);  
        return dist * 60 * 1.1515 * 1.609344;
    }
	
	public static void main(String[] args) {
		System.out.println(geo_distance(121.491909,31.233234,121.411994,31.206134)) ;
		System.out.println(geo_distance(117.107277,31.980298,117.524757,31.888227)) ;
		System.out.println(distance(114.419547,30.511122,114.418692,30.504468)) ;
		System.out.println(distance(117.107277,31.980298,117.524757,31.888227)) ;
		
		System.out.println(geo_distance(114.374446,30.521626,114.373058,30.514447)) ;
		System.out.println(distance(114.374446,30.521626,114.373058,30.514447)) ;

	}
}
