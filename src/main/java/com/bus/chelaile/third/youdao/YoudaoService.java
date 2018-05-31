package com.bus.chelaile.third.youdao;



import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.third.ThirdAdData;


public class YoudaoService {

	//private static final Logger logger = LoggerFactory.getLogger(YoudaoService.class);
	
	/**
	 * 有道
	 * @param ap
	 * @param type
	 * @param filterIds	需要过滤的广告id,用逗号分隔
	 * @return
	 */
	public static ThirdAdData getYouDaoData(AdvParam ap, ShowType type,String filterIds) {
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

//	/**
//	 * 
//	 * @param ap
//	 * @param type
//	 *            0:开屏，1：首页-左文右图广告
//	 * @throws UnsupportedEncodingException
//	 */
//	public void doService(AdvParam ap, int type) throws UnsupportedEncodingException {
//		RequestModel model = getDeviceModel(ap, type);
//		String context = null;
//		try {
//			context = JsonBinder.toJson(model, JsonBinder.nonNull);
//		} catch (Exception e) {
//			logger.error(e.getMessage(), e);
//		}
//		 String result =
//		 YoudaoRequestResponseManager.fetchAdResponseAsString(context);
//		 String test1 = new String(result.getBytes(),"utf-8");
//		System.out.println(test1);
//	}
//
//	private RequestModel getDeviceModel(AdvParam ap, int type) {
//		RequestModel m = new RequestModel();
//		m.setId("e3f49841bbd3ceb0c6a531ca32f4a754");
//		m.setAv(ap.getV());
//		m.setCt(2);
//		m.setDct(0);
//		m.setUdid(ap.getIdfa());
//
//		m.setAuidmd5(DigestUtils.md5Hex(ap.getIdfa()));
//		m.setImei(ap.getIdfa());
//		m.setImeimd5(DigestUtils.md5Hex(ap.getIdfa()));
//		m.setRip(ap.getIp());
//		m.setLl(ap.getLng() + "," + ap.getLat());
//		m.setLla("73.0");
//		m.setLlt("1");
//		m.setLlp("p");
//		m.setWifi("\"车来了_普通\"");
//
//		return m;
//	}
//
//	/**
//	 * "id": "faf49b07805cca0a38097a732f388462", "udid":
//	 * "BA8C0E13-F99A-4294-BABA-1489C33E9B6D", "imei":
//	 * "BA8C0E13-F99A-4294-BABA-1489C33E9B6D", "lla": "73.0", "llp": "p", "wifi":
//	 * "\"车来了_普通\"", "rip": "10.168.0.10", "imeimd5":
//	 * "BA8C0E13-F99A-4294-BABA-1489C33E9B6D", "ct": 2, "dct": 0, "ll":
//	 * "116.403538,39.994026", "auidmd5": "305612168a059fc9ccdac8d95d99e485", "av":
//	 * "5.50.0", "llt": "1"
//	 * 
//	 * @param args
//	 * @throws UnsupportedEncodingException
//	 */
//	public static void main(String[] args) throws UnsupportedEncodingException {
//		
//		String az = DigestUtils.md5Hex("BA8C0E13-F99A-4294-BABA-1489C33E9B6D");
//		
//		az = az.toUpperCase();
//
//		YoudaoRequestResponseManager ym = new YoudaoRequestResponseManager();
//
//		String context = "id=e3f49841bbd3ceb0c6a531ca32f4a754&udid=BA8C0E13-F99A-4294-BABA-1489C33E9B6D&"
//				+ "imei=BA8C0E13-F99A-4294-BABA-1489C33E9B6D&lla=73.0&llp=p&wifi=&rip=10.168.0.10&"
//				+ "imeimd5=305612168A059FC9CCDAC8D95D99E485&ct=2&dct=0&ll=116.403538,39.994026&auidmd5=305612168A059FC9CCDAC8D95D99E485&av=5.50.0&llt=1";
//
//		String value = ym.startGet(context);
//		
//		System.out.println(value);
//
//		YoudaoService s = new YoudaoService();
//		AdvParam ap = new AdvParam();
//		ap.setDpi("2.000000");
//		ap.setS("ios");
//		ap.setUdid("d41d8cd98f00b204e9800998ecf8427e089ec208");
//		ap.setIdfa("BA8C0E13-F99A-4294-BABA-1489C33E9B6D");
//		ap.setIdfv("92669482-B539-4E4C-BCE3-92829225F5BB");
//		ap.setSv("10.3.3");
//		// ap.setMac(mac);
//
//		ap.setV("5.50.0");
//		ap.setNw("2g");
//		ap.setIp("10.168.0.10");
//	//	ap.setW("320.000000");
//		ap.setScreenHeight(480);
//		ap.setDeviceType("iPhone5c");
//		ap.setUa(
//				"Mozilla/5.0%20(iPhone;%20CPU%20iPhone%20OS%2010_3_3%20like%20Mac%20OS%20X)%20AppleWebKit/603.3.3%20(KHTML,%20like%20Gecko)%20Mobile/14G5037b");
//		ap.setLng(116.403538);
//		ap.setLat(39.994026);
//
//		s.doService(ap, 1);
//	}

}
