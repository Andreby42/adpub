package com.bus.chelaile.util.gpsdistance;



import java.util.ArrayList;
import java.util.List;



public class MileageCompute {
	public static double compute(double jingdu, double weidu, List<BusSosTrajectory> tralist, 
			List<Double> tramiles) {
		double mileage = 0;
		double minDis=100000000;
		GPSPoint point = new GPSPoint(jingdu, weidu);
		for (int i=0; i<tralist.size()-1; i++) {
			Segment segment=new Segment(
					new GPSPoint(tralist.get(i).getJingdu(),tralist.get(i).getWeidu()),
					new GPSPoint(tralist.get(i+1).getJingdu(),tralist.get(i+1).getWeidu())
			);
			
			double dis=Tools.nearestDistance(segment,point);
			double len = 0.0;
			if (i == 0) {
				len = Tools.pointShadow(segment,point);
			} else {
				/*double d = Tools.pointShadow(segment,point);
				double d2 = tramiles.get(i-1);*/
				len = tramiles.get(i) + Tools.pointShadow(segment,point);
			}
			if (dis < 20) {
				// 站点距离脊线的最短距离为20M时，认为此时站点距离这小段脊线最近
				minDis = dis;
				mileage = len;
				break;
			}
			if (dis < minDis) {
				minDis = dis;
				mileage = len;
			}
			
		}
		
		return mileage;
	}
	
	public static List<Double> tramiles(List<BusSosTrajectory> tralist) {
		List<Double> tramiles = new ArrayList<Double>();
		tramiles.add(0.0);
		for (int i=1; i<tralist.size(); i++) {
			tramiles.add(tramiles.get(i-1) 
					+ Tools.gpsDistance(new GPSPoint(tralist.get(i-1).getJingdu(), tralist.get(i-1).getWeidu()), 
							new GPSPoint(tralist.get(i).getJingdu(), tralist.get(i).getWeidu())));
		}
		
		
		return tramiles;
	}
	
	
}
