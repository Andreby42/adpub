package com.bus.chelaile.util.gpsdistance;



import java.util.List;

/**
 * Created by Yang on 2015/2/5.
 */
public class Tools {
	public static void main(String[] args) {
		System.out.println(Tools.gpsDistance(new GPSPoint(114.893899,40.837209), new GPSPoint(114.894159,40.837918)));
	}
	public static double gpsDistance(GPSPoint point1,GPSPoint point2) {
		double r = 6378137;
		double lat1 = Math.toRadians(point1.lat);
		double lon1 = Math.toRadians(point1.lon);
		double lat2 = Math.toRadians(point2.lat);
		double lon2 = Math.toRadians(point2.lon);
		double d1 = Math.abs(lat1 - lat2);
		double d2 = Math.abs(lon1 - lon2);
		double p = Math.pow(Math.sin(d1 / 2), 2) + Math.cos(lat1)
				* Math.cos(lat2) * Math.pow(Math.sin(d2 / 2), 2);
		double dis = r * 2 * Math.asin(Math.sqrt(p));
		return dis;
	}

	public static double distance(GPSPoint point1,GPSPoint point2) {
		return Math.sqrt((point1.lon-point2.lon)*(point1.lon-point2.lon)+(point1.lat-point2.lat)*(point1.lat-point2.lat));
	}

	public static double distance2(GPSPoint point1,GPSPoint point2) {
		return (point1.lon-point2.lon)*(point1.lon-point2.lon)+(point1.lat-point2.lat)*(point1.lat-point2.lat);
	}

	public static double cross(GPSPoint point1,GPSPoint point2) {
		return point1.lon*point2.lat-point1.lat*point2.lon;
	}

	public static double dot(GPSPoint point1,GPSPoint point2) {
		return point1.lon*point2.lon+point1.lat*point2.lat;
	}

	public static double nearestDistance(Segment segment,GPSPoint point) {
		double disPA=distance2(point,segment.start);
		double disPB=distance2(point,segment.end);
		if (disPA<1e-10) return gpsDistance(point,segment.start);
		if (disPB<1e-10) return gpsDistance(point,segment.end);
		double disAB=distance2(segment.start,segment.end);
		if (disAB<1e-10) return gpsDistance(point,segment.start);
		if (disPA>=disAB+disPB) return gpsDistance(point,segment.end);
		if (disPB>=disAB+disPA) return gpsDistance(point,segment.start);

		GPSPoint vectorAB=new GPSPoint(segment.end.lon-segment.start.lon,segment.end.lat-segment.start.lat);
		GPSPoint vectorAP=new GPSPoint(point.lon-segment.start.lon,point.lat-segment.start.lat);
		if (Math.abs(cross(vectorAP,vectorAB))<1e-10) return 0;
		double sinAlpha=0;
		if (disPA*disAB!=0) {
			sinAlpha=cross(vectorAP,vectorAB)/(Math.sqrt(disPA)*Math.sqrt(disAB));
		}
		double gpsAP=gpsDistance(segment.start,point);
		return Math.abs(gpsAP*sinAlpha);
	}

	public static double pointShadow(Segment segment,GPSPoint point) {
		GPSPoint vectorAB=new GPSPoint(segment.end.lon-segment.start.lon,segment.end.lat-segment.start.lat);
		GPSPoint vectorAP=new GPSPoint(point.lon-segment.start.lon,point.lat-segment.start.lat);
		double cosAlpha=0;
		double disPA=distance(point,segment.start);
		double disAB=distance(segment.start,segment.end);
		if (disPA*disAB!=0) {
			cosAlpha=dot(vectorAP,vectorAB)/(distance(point,segment.start)*distance(segment.start,segment.end));
		}
		double gpsAP=gpsDistance(segment.start,point);
		return gpsAP*cosAlpha;
	}

	public static double getPointMileageOnRoutes(BusLine line, int dir, GPSPoint point, double start){
		List<BusLine.Routes> routes=null;
		if (dir==0)
			routes=line.routes1;
		else
			routes=line.routes2;

		double minDis=100000000;
		double result=0;
		for (int i=0;i<routes.size()-1;i++) {
			Segment segment=new Segment(
					new GPSPoint(routes.get(i).lon,routes.get(i).lat),
					new GPSPoint(routes.get(i+1).lon,routes.get(i+1).lat)
			);

			double dis=nearestDistance(segment,point);
			double len=line.sumRoutes.get(dir).get(i)+pointShadow(segment,point);
			if (dis<20 && len>start-20) {
				minDis=dis;
				result=len;
				break;
			}

			if (dis<minDis) {
				minDis=dis;
				result=len;
			}
		}

		return result;
	}
}
