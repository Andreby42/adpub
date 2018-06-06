package com.bus.chelaile.third.zhongmeng;

import org.apache.commons.codec.digest.DigestUtils;

import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.third.ThirdAdData;

public class ZhongMengService {
	public static ThirdAdData getZhongmengData(AdvParam ap, ShowType type,String filterIds) {
		String url = "https://gorgon.youdao.com/gorgon/request.s?id=";
		String id = null;
		if (type.getValue() == ShowType.OPEN_SCREEN.getValue()) {
			if (ap.getS().equalsIgnoreCase("ios")) {
				id = "faf49b07805cca0a38097a732f388462";
			} else {
				id = "31b89bd78e296d43ca0b1128779613cc";
			}
		} else if (type.getValue() == ShowType.DOUBLE_COLUMN.getValue()) {
			if (ap.getS().equalsIgnoreCase("ios")) {
				id = "05de0f72d83e44de913534cecda03451";
			} else {
				id = "7a7b059ca39624f1c1ec24fa2ad375f6";
			}
		} else if (type.getValue() == ShowType.LINE_FEED_ADV.getValue()) {
			if (ap.getS().equalsIgnoreCase("ios")) {
				id = "e3f49841bbd3ceb0c6a531ca32f4a754";
			} else {
				id = "aae3f1e9fd3c10be479138b6b1288530";
			}
		} else if (type.getValue() == ShowType.STATION_ADV.getValue()) {
			if (ap.getS().equalsIgnoreCase("ios")) {
				id = "0fe897890119007664cfd009556ce283";
			} else {
				id = "59856e17db1d73fb3dbcb5af6c1ab10f";
			}
		}

		url += id;
		url +=  "&av=" + ap.getV() + "&ll=" + ap.getLng() + "," + ap.getLat() + "&lla="
				+ ap.getGpsAccuracy()+"&llt=1&llp=p";
		if( ap.getWifissid() != null && !ap.getWifissid().equals("") ) {
			url += "&wifi=" + ap.getWifissid();
		}
		
		if( ap.getAndroidID() != null && !ap.getAndroidID().equals("") ) {
			url += "&udid=" + ap.getAndroidID().toUpperCase();
			url += "&auidmd5=" + DigestUtils.md5Hex(ap.getAndroidID().toUpperCase()).toUpperCase();
		}else {
			url += "&udid=" + ap.getIdfa().toUpperCase();
			url += "&auidmd5=" + DigestUtils.md5Hex(ap.getIdfa().toUpperCase()).toUpperCase();
		}
		
		
		
		if( ap.getImei() != null ) {
			url += "&imei=" + ap.getImei();
			url += "&imeimd5=" + DigestUtils.md5Hex(ap.getImei()).toUpperCase();
		}else {
			url += "&imei=" + ap.getIdfa().toUpperCase();
			url += "&imeimd5=" + DigestUtils.md5Hex(ap.getIdfa().toUpperCase()).toUpperCase();
		}
		
		
		int netWork = 0;
		int ctType = 0;
		if (ap.getNw() != null) {
			if (ap.getNw().equalsIgnoreCase("2g")) {
				netWork = 11;
				ctType = 3;
			} else if (ap.getNw().equalsIgnoreCase("3g")) {
				netWork = 12;
				ctType = 3;
			} else if (ap.getNw().equalsIgnoreCase("4g")) {
				netWork = 13;
				ctType = 3;
			}else if (ap.getNw().equalsIgnoreCase("wifi")) {
				ctType = 2;
			}
		}
		
		url += "&ct=" + ctType + "&dct="+netWork;
		
		if(filterIds != null) {
			url += "&cids=" + filterIds;
		}
		
		
		ThirdAdData data = new ThirdAdData();
		data.setUrl(url);
		data.setOk(true);
		return data;
		
	}
}
