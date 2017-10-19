package com.bus.chelaile.util;




public class GPSConvert {
	static double pi = 3.14159265358979324; 
	static double x_pi = pi * 3000.0 / 180.0; 
	static double a = 6378245.0;
	static double ee = 0.00669342162296594323;
	private static double MAXDIS=0.5;
	private static double MAXERRDIS=4;
	
	public static void main(String[] args) {
		YGLocation baidu1 = new YGLocation(112.979353,28.213478) ;
		YGLocation wgs = bd2wgs(baidu1);
		//YGLocation baidu2 = wgs2bd(wgs);
	//	System.out.println(wgs.longitude+","+wgs.latitude+","+distanceFromBaiduToBaidu(baidu1, baidu2));
		
		YGLocation bd = wgs2bd(baidu1);
		
//		double sjingdu = 120.296042;
//		double sweidu = 36.063341;
//		double jingdu = 120.30766900025 ;
//		double weidu = 36.069121959576 ;
//		double[] gcj = wgs2gcj(sjingdu,sweidu);
//		double[] bd  = gcj2bd(gcj[0],gcj[1]);
	}
	
	public static double[] wgs2bd(double lon,double lat){
		double[] gcj = wgs2gcj(lon,lat);
		double[] bd  = gcj2bd(gcj[0],gcj[1]);
		return bd ;
	}

	
	public static double[] gcj2bd(double gg_lon,double gg_lat) 
	{ 
		double bd_lat, bd_lon;
	    double x = gg_lon, y = gg_lat; 
	    double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi); 
	    double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi); 
	    bd_lon = z * Math.cos(theta) + 0.0065; 
	    bd_lat = z * Math.sin(theta) + 0.006; 
	    return new double[]{bd_lon,bd_lat};
	} 
	
	public static double[] bd2gcj(double lon, double lat) 
	{ 
/*		double bd_lat, bd_lon;
	    double x = gg_lon, y = gg_lat; 
	    double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi); 
	    double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi); 
	    bd_lon = z * Math.cos(theta) + 0.0065; 
	    bd_lat = z * Math.sin(theta) + 0.006; 
	    System.out.println(bd_lat+"/"+bd_lon);
	    return new double[]{bd_lon,bd_lat};*/
		YGLocation yg = new YGLocation(lon, lat);
		YGLocation wgs = bd2wgs(yg);
		double[] gcj = wgs2gcj(wgs.longitude, wgs.latitude); 
		return gcj;
	} 
	
	

        public static double[] wgs2gcj(double wgLon,double wgLat)
        {
//        	if(outOfChina(wgLat,wgLon)){
//        		return null;
//        	}
            double dLat = transformLat(wgLon - 105.0, wgLat - 35.0);
            double dLon = transformLon(wgLon - 105.0, wgLat - 35.0);
            double radLat = wgLat / 180.0 * pi;
            double magic = Math.sin(radLat);
            magic = 1 - ee * magic * magic;
            double sqrtMagic = Math.sqrt(magic);
            dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
            dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
            return new double[]{wgLon + dLon,wgLat + dLat};
        }

        static boolean outOfChina(double lat, double lon)
        {
            if (lon < 72.004 || lon > 137.8347)
                return true;
            if (lat < 0.8293 || lat > 55.8271)
                return true;
            return false;
        }

        static double transformLat(double x, double y)
        {
            double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
            ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
            ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0;
            ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0;
            return ret;
        }

        static double transformLon(double x, double y)
        {
            double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
            ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
            ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;
            ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0 * pi)) * 2.0 / 3.0;
            return ret;
        }
        
      //将角度转换为弧度  
    	 static double deg2rad(double degree) {  
    	        return degree / 180 * Math.PI;  
    	}  
    	//将弧度转换为角度  
    	 static double rad2deg(double radian) {  
    	        return radian * 180 / Math.PI;  
    	}  
    	
    	/**
    	 * 计算两个GPS点直线距离
    	 * 结果单位：M
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
            if(dist>1) dist = 1.0;
            dist = Math.acos(dist);  
            dist = rad2deg(dist);  
            return dist * 60 * 1.1515 * 1.609344 * 1000;
        }


 /*   	public static YGLocation bd2wgsplus(YGLocation baidu) throws Exception
    	{
    	    YGLocation wgs = bd2wgs(baidu);
    	    for(int i=0;i<=5;i++)
    	    if(distanceFromWgsToBaidu(wgs,baidu)>MAXERRDIS){
    	        wgs = bd2wgs(baidu);
    	    }
    	    return wgs;
    	}
 */   	
    	public static YGLocation bd2wgs(YGLocation baidu) 
    	{
    		return bd2wgsPlus(baidu);
    	}
    	public static YGLocation bd2wgsPlus(YGLocation baidu)
    	{
    	    mjw mjw = new mjw();
    	    
    	    double dis = adpate(baidu,0.0,mjw);
    	    dis = adpate(baidu,0.1,mjw);
    	    dis = adpate(baidu,0.01,mjw);
    	    dis = adpate(baidu,0.001,mjw);
    	    
    	    
    	    double minmw=mjw.mw,minmj=mjw.mj,mindis=dis;
    	    
    	    for(int i=0;i<30;i++){
    	        if(dis>MAXDIS){
    	        		dis = adpate(baidu ,0.0001,mjw);
    	        	if(dis>MAXDIS)
    	       			dis = adpate(baidu ,0.00001,mjw);
    	       		if(dis>MAXDIS)
    	       			dis = adpate(baidu ,0.000001,mjw);

    	            
    	            if(mindis>=dis){
    	                mindis = dis;
    	                minmw = mjw.mw;
    	                minmj = mjw.mj;
    	            }
    	        }else{
    	        	break;
    	        }
    	    }
    	    
    	    mjw.mw=minmw;mjw.mj=minmj;dis=mindis;
    	    
    	    return new YGLocation(baidu.longitude+mjw.mj,baidu.latitude+mjw.mw);
    	}
    	

    	public static YGLocation wgs2bd(YGLocation wgs) 
    	{
    		double[] bd = wgs2bd(wgs.longitude,wgs.latitude);
    		return new YGLocation(bd[0],bd[1]);
    	}

    	public static double distanceFromBaiduToBaidu(YGLocation baidu1 , YGLocation baidu2)
    	{
    	    return distance(baidu1.longitude,baidu1.latitude,baidu2.longitude,baidu2.latitude);
    	}

    	private static double distanceFromWgsToBaidu (YGLocation wgs ,YGLocation baidu) 
    	{
    	    return distanceFromBaiduToBaidu(wgs2bd(wgs),baidu);
    	}

    	

    	private static double adpate(YGLocation baidu,double p,mjw mjw)
    	{
    	    double dis = distanceFromWgsToBaidu(new YGLocation(baidu.longitude+mjw.mj,baidu.latitude+mjw.mw),baidu);

    	    //lng adpate
    	    double dis1 = distanceFromWgsToBaidu(new YGLocation(baidu.longitude+mjw.mj+p,baidu.latitude+mjw.mw),baidu);

    	    
    	    if(dis1<dis){
    	        mjw.mj = mjw.mj+p ;
    	        do{
    	            double tmpdis = dis1 ;
    	            dis1 = distanceFromWgsToBaidu(new YGLocation(baidu.longitude+mjw.mj+p,baidu.latitude+mjw.mw),baidu);
    	            if(dis1<tmpdis){
    	                mjw.mj = mjw.mj+p;
    	            }else{
    	                break;
    	            }
    	        }while(true);
    	    }else{
    	        dis1 = distanceFromWgsToBaidu(new YGLocation(baidu.longitude+mjw.mj-p,baidu.latitude+mjw.mw),baidu);
    	        if(dis1<dis){
    	            mjw.mj = mjw.mj-p;
    	            do{
    	                double tmpdis = dis1 ;
    	                dis1 = distanceFromWgsToBaidu(new YGLocation(baidu.longitude+mjw.mj-p,baidu.latitude+mjw.mw),baidu);
    	                if(dis1<tmpdis){
    	                    mjw.mj = mjw.mj-p;
    	                }else{
    	                    break;
    	                }
    	            }while(true);
    	        }
    	    }
    	    
    	    
    	    dis = distanceFromWgsToBaidu(new YGLocation(baidu.longitude+mjw.mj,baidu.latitude+mjw.mw),baidu);
    	    //lat adpate
    	    double dis2 = distanceFromWgsToBaidu(new YGLocation(baidu.longitude+mjw.mj,baidu.latitude+mjw.mw+p),baidu);
    	    
    	    if(dis2<dis){
    	        mjw.mw = mjw.mw+p;
    	        do{
    	            double tmpdis = dis2;
    	            dis2 = distanceFromWgsToBaidu(new YGLocation(baidu.longitude+mjw.mj ,baidu.latitude+mjw.mw+p),baidu);
    	            if(dis2<tmpdis){
    	                mjw.mw = mjw.mw+p;
    	            }else{
    	                break;
    	            }
    	        }while(true);
    	    }else{
    	        dis2 = distanceFromWgsToBaidu(new YGLocation(baidu.longitude+mjw.mj,baidu.latitude+mjw.mw-p),baidu);
    	        if(dis2<dis){
    	            mjw.mw = mjw.mw-p;
    	            do{
    	                double tmpdis = dis2;
    	                dis2 = distanceFromWgsToBaidu(new YGLocation(baidu.longitude+mjw.mj,baidu.latitude+mjw.mw-p),baidu);
    	                if(dis2<tmpdis){
    	                    mjw.mw = mjw.mw-p;
    	                }else{
    	                    break;
    	                }
    	            }while(true);
    	        }
    	        
    	    }
    	    
    	    dis = distanceFromWgsToBaidu(new YGLocation(baidu.longitude+mjw.mj,baidu.latitude+mjw.mw),baidu);
    	    
    	    return dis ;
    	}

    }

    class mjw {
    	double mj ;
    	double mw ;
    	
    	public mjw(){
    		
    	}
    	
    	public mjw(double mj,double mw){
    		this.mj = mj;
    		this.mw = mw;
    	}
    }
