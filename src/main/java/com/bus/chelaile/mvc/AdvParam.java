package com.bus.chelaile.mvc;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.bus.chelaile.model.ads.Station;



public class AdvParam {
	 private String userId;
     private String accountId;
     // 需要保证每一个s的值都是小写的。
     private String s;
     private String src;
     private String udid;
     private double lng;
     private double lat;
     private String cityId;
     private int cityState;
     private String first_src;
     private String last_src;
     private String v;
     private int vc;
     private String stnList;
     private List<Station> stationList;

     // 线路也广告的参数 
     private String lineId;
     private String lineNo;
     private String lineName;
     private int lnState;
     private String stnName;
     private int stnOrder;
     
     private String h5Src;
     private String h5User;

     private String nw;
     private int type;
   //  private String remote_addr;
     
   //  private String x_forwarded_for;
     private String ua;
     private String idfa;
     private String imei;
     private String o1;
     private String gpid;
     private String ip;
     private String sv;
     
     private int screenHeight;
     private String deviceType;
     private String InState;
     private String shareId;
    // private String direction;	//线路方向

     private String picHeight;	//要求给url加上的高度和宽度
     private String picWidth ;
     
     private String statsAct; //stats_act,目前用于刷新信息流
     private String refer; //refer 用于统计信息流请求来源，分discovrery和linedetail
     private int gridLines; //标记首页有几行 导流入口 广告
     private String AndroidID; //AndroidId
     private String mac; //mac地址
     
     private String cshow; // 详情页调用场景
     private int distance; //用户距离首页第一个站点的距离
     private int rideStatus; //骑行状态 // 0没骑车 ，1骑行中， 3骑行结束
     
     private String stationId; // 站点id
     private int startMode; // 冷热启动模式， 0 冷启动， 1 热启动
     
     /**
      * 存入的s都会被转换为小写。
      *
      * @param plat
      */
     public void setS(String plat) {
    	 if(StringUtils.isNoneBlank(plat))
    		 s = plat.toLowerCase();
    	 else
    		 s = plat;
     }


	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}


	public String getAccountId() {
		return accountId;
	}


	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}


	public String getUdid() {
		return udid;
	}


	public void setUdid(String udid) {
		this.udid = udid;
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


	public String getCityId() {
		return cityId;
	}


	public void setCityId(String cityId) {
		this.cityId = cityId;
	}


	public int getCityState() {
		return cityState;
	}


	public void setCityState(int cityState) {
		this.cityState = cityState;
	}


	public String getFirst_src() {
		return first_src;
	}


	public void setFirst_src(String first_src) {
		this.first_src = first_src;
	}


	public String getLast_src() {
		return last_src;
	}


	public void setLast_src(String last_src) {
		this.last_src = last_src;
	}


	public String getV() {
		return v;
	}


	public void setV(String v) {
		this.v = v;
	}


	public int getVc() {
		return vc;
	}


	public void setVc(int vc) {
		this.vc = vc;
	}


	public String getStnList() {
		return stnList;
	}


	public void setStnList(String stnList) {
		this.stnList = stnList;
	}


	public List<Station> getStationList() {
		return stationList;
	}


	public void setStationList(List<Station> stationList) {
		this.stationList = stationList;
	}


	public String getLineId() {
		return lineId;
	}


	public void setLineId(String lineId) {
		this.lineId = lineId;
	}


	public String getLineNo() {
		return lineNo;
	}


	public void setLineNo(String lineNo) {
		this.lineNo = lineNo;
	}


	public String getLineName() {
		return lineName;
	}


	public void setLineName(String lineName) {
		this.lineName = lineName;
	}


	public int getLnState() {
		return lnState;
	}


	public void setLnState(int lnState) {
		this.lnState = lnState;
	}


	public String getStnName() {
		return stnName;
	}


	public void setStnName(String stnName) {
		this.stnName = stnName;
	}


	public int getStnOrder() {
		return stnOrder;
	}


	public void setStnOrder(int stnOrder) {
		this.stnOrder = stnOrder;
	}


	public String getH5Src() {
		return h5Src;
	}


	public void setH5Src(String h5Src) {
		this.h5Src = h5Src;
	}


	public String getH5User() {
		return h5User;
	}


	public void setH5User(String h5User) {
		this.h5User = h5User;
	}


	public String getNw() {
		return nw;
	}


	public void setNw(String nw) {
		this.nw = nw;
	}


	public int getType() {
		return type;
	}


	public void setType(int type) {
		this.type = type;
	}


	public String getUa() {
		return ua;
	}


	public void setUa(String ua) {
		this.ua = ua;
	}


	public String getIdfa() {
		return idfa;
	}


	public void setIdfa(String idfa) {
		this.idfa = idfa;
	}


	public String getImei() {
		return imei;
	}


	public void setImei(String imei) {
		this.imei = imei;
	}


	public String getO1() {
		return o1;
	}


	public void setO1(String o1) {
		this.o1 = o1;
	}


	public String getGpid() {
		return gpid;
	}


	public void setGpid(String gpid) {
		this.gpid = gpid;
	}


	public String getIp() {
		return ip;
	}


	public void setIp(String ip) {
		this.ip = ip;
	}


	public String getSv() {
		return sv;
	}


	public void setSv(String sv) {
		this.sv = sv;
	}


	public int getScreenHeight() {
		return screenHeight;
	}


	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}


	public String getDeviceType() {
		return deviceType;
	}


	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}


	public String getS() {
		return s;
	}


	public String getInState() {
		return InState;
	}


	public void setInState(String inState) {
		InState = inState;
	}


	public String getShareId() {
		return shareId;
	}


	public void setShareId(String shareId) {
		this.shareId = shareId;
	}


	public String getPicHeight() {
		return picHeight;
	}


	public void setPicHeight(String picHeight) {
		this.picHeight = picHeight;
	}


	public String getPicWidth() {
		return picWidth;
	}


	public void setPicWidth(String picWidth) {
		this.picWidth = picWidth;
	}


	public String getStatsAct() {
		return statsAct;
	}


	public void setStatsAct(String statsAct) {
		this.statsAct = statsAct;
	}


	public int getGridLines() {
		return gridLines;
	}


	public void setGridLines(int gridLines) {
		this.gridLines = gridLines;
	}


	public String getAndroidID() {
		return AndroidID;
	}


	public void setAndroidID(String androidID) {
		AndroidID = androidID;
	}


	public String getMac() {
		return mac;
	}


	public void setMac(String mac) {
		this.mac = mac;
	}


	public String getRefer() {
		return refer;
	}


	public void setRefer(String refer) {
		this.refer = refer;
	}


	public String getCshow() {
		return cshow;
	}


	public void setCshow(String cshow) {
		this.cshow = cshow;
	}


	public int getDistance() {
		return distance;
	}


	public void setDistance(int distance) {
		this.distance = distance;
	}


	public int getRideStatus() {
		return rideStatus;
	}


	public void setRideStatus(int rideStatus) {
		this.rideStatus = rideStatus;
	}


	public String getSrc() {
		return src;
	}


	public void setSrc(String src) {
		this.src = src;
	}


	public String getStationId() {
		return stationId;
	}


	public void setStationId(String stationId) {
		this.stationId = stationId;
	}


	public int getStartMode() {
		return startMode;
	}


	public void setStartMode(int startMode) {
		this.startMode = startMode;
	}
	
	
}
