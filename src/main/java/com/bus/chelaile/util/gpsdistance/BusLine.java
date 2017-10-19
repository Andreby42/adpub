package com.bus.chelaile.util.gpsdistance;



import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yang on 2015/2/5.
 */
public class BusLine {

	public class Station {
		public String name;
		public double lon;
		public double lat;
		public int index;
	}

	public class Routes {
		public double lon;
		public double lat;
	}

	public String name;
	public String number;
	public String company;
	public String start;
	public String end;

	public List<Station> stations1;
	public List<Station> stations2;

	public List<Routes> routes1;
	public List<Routes> routes2;

	public List<List<Double>> sumRoutes;

	public List<List<Double>> stationMileage;

	public BusLine() {
		sumRoutes=new ArrayList<List<Double>>();
		stationMileage=new ArrayList<List<Double>>();
	}
}
