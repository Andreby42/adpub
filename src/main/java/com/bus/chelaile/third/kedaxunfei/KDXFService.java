package com.bus.chelaile.third.kedaxunfei;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.third.ThirdAdData;

import com.bus.chelaile.third.kedaxunfei.model.RequestModel;
import com.bus.chelaile.util.JsonBinder;

/**
 * 科大讯飞
 * 
 * @author 41945
 *
 */
public class KDXFService {
	
	
	
    private static final Logger logger = LoggerFactory
            .getLogger(KDXFService.class);
//    /**
//     * 
//     * @param ap
//     * @param type	0:开屏，1：首页-左文右图广告
//     * @throws UnsupportedEncodingException
//     */
	public void doService(AdvParam ap, ShowType type) throws Exception {
		ThirdAdData model = getKDXF(ap, type);
		String context = model.getData();
		System.out.println(context);
//		try {
//			context = JsonBinder.toJson(model., JsonBinder.nonNull);
//		} catch (Exception e) {
//			logger.error(e.getMessage(),e);
//		}
		String result = KDXFRequestResponseManager.fetchAdResponseAsString(context);
		String test1 = new String(result.getBytes(),"utf-8");
		System.out.println(test1);
	}

	public static  ThirdAdData getKDXF(AdvParam ap, ShowType type) throws Exception {
		RequestModel m = new RequestModel();
		m.setDevicetype("0");
		if (ap.getS().equalsIgnoreCase("android")) {
			m.setOs("Android");
			m.setAdid(ap.getAndroidID());
			m.setImei(ap.getImei());
			m.setAppid("5add7ce1");
			m.setPkgname("com.ygkj.chelaile.standard");
			m.setIsboot("0");
			if (type.getValue() == ShowType.OPEN_SCREEN.getValue()) {
				m.setIsboot("1");
				m.setAdunitid("D028C0ADDDBC38952DA01241B4939E64");
			} else if( type.getValue() == ShowType.DOUBLE_COLUMN.getValue() ){
				
				m.setAdunitid("C23BFCFFE1F3D8D5C06D7E1AEEA83812");
			}else if( type.getValue() == ShowType.STATION_ADV.getValue() ){
				
				m.setAdunitid("2CF5CD0015BC4E926778633B8AD0D9AE");
			}else if( type.getValue() == ShowType.LINE_FEED_ADV.getValue() ){
				
				m.setAdunitid("5CBF4E804C06EBF6EEAF93DC5EA6BBCF");
			}else if( type.getValue() == ShowType.LINE_RIGHT_ADV.getValue() ){
				
				m.setAdunitid("64D22F2F7B704AC852B7EE6C9A3E1395");
			}

			m.setVendor(ap.getVendor());
		} else {
			m.setOs("iOS");
			m.setAppid("5acf1d60");
			m.setOpenudid(ap.getUdid());
			m.setIdfa(ap.getIdfa());
			m.setIdfv(ap.getIdfv());
			m.setIsboot("0");
			m.setPkgname("com.chelaile.lite");
			if (type.getValue() == ShowType.OPEN_SCREEN.getValue()) {
				m.setIsboot("1");
				m.setAdunitid("46232AB17BDA70BED71794AD4915D12A");
			} else if( type.getValue() == ShowType.DOUBLE_COLUMN.getValue() ){
				
				m.setAdunitid("5F7EDBCCC6C116C07DBB40EB9A937F4E");
			}else if( type.getValue() == ShowType.STATION_ADV.getValue() ){
				
				m.setAdunitid("70EB266B2C8429B0B6C10D9B6F9BFA93");
			}else if( type.getValue() == ShowType.LINE_FEED_ADV.getValue() ){
				
				m.setAdunitid("2D8857EE0D286E80203F7334F8356B1C");
			}else if( type.getValue() == ShowType.LINE_RIGHT_ADV.getValue() ){
				
				m.setAdunitid("F6FDE2EB25CE4A39AC4ED6F3D495F269");
			}
			
			
			m.setVendor("apple");
		}
		m.setOsv(ap.getSv());
		if( ap.getMac() != null ) {
			m.setMac(ap.getMac());
		}
		
		m.setDensity(ap.getDpi());
		m.setAppver(ap.getV());
		/**
		 * 1—Ethernet ， 2—wifi，3—蜂窝网 络，未知代，4—， 2G，5—蜂窝网络， 3G，6—蜂窝网络， 4G 2g 3g 4g wifi
		 * unknow
		 */
		if (ap.getNw() != null) {
			if (ap.getNw().equalsIgnoreCase("2g")) {
				m.setNet("4");
			} else if (ap.getNw().equalsIgnoreCase("3g")) {
				m.setNet("5");
			} else if (ap.getNw().equalsIgnoreCase("4g")) {
				m.setNet("6");
			} else if (ap.getNw().equalsIgnoreCase("wifi")) {
				m.setNet("2");
			}
		}
		
		m.setIp(ap.getIp());
		m.setUa(ap.getUa());
		m.setTs(System.currentTimeMillis() / 1000 + "");

		m.setDvw(ap.getScreenWidth() +"");
		m.setDvh(ap.getScreenHeight() + "");
		m.setOrientation("0");
	
		m.setModel(ap.getDeviceType());
		m.setGeo(ap.getLng() + "," + ap.getLat());
		//test
		//m.setAdunitid("2D8857EE0D286E80203F7334F8356B1C");
		
		if (type.getValue() == ShowType.OPEN_SCREEN.getValue()) {
			m.setAdh("960");
			m.setAdw("640");
		} else if( type.getValue() == ShowType.DOUBLE_COLUMN.getValue() ){
			m.setAdh("92");
			m.setAdw("359");
		}else if( type.getValue() == ShowType.STATION_ADV.getValue() ){
			m.setAdh("92");
			m.setAdw(ap.getScreenWidth()+"");
		}else if( type.getValue() == ShowType.LINE_FEED_ADV.getValue() ){
			m.setAdh("92");
			m.setAdw(ap.getScreenWidth()+"");
		}

		ThirdAdData data = new ThirdAdData();
		data.setUrl("https://cs.voiceads.cn/ad/request");
		data.setData(JsonBinder.toJson(m, JsonBinder.nonNull));
		return data;
		
	}
	
	public static void main(String[] args) throws Exception {
		KDXFService s = new KDXFService();
		AdvParam ap = new AdvParam();
//		ap.setDpi("2.000000");
//		ap.setS("ios");
//		ap.setUdid("d41d8cd98f00b204e9800998ecf8427e089ec208");
//		ap.setIdfa("F91D631D-B0BF-4F79-9E8E-0331F18E0184");
//		ap.setIdfv("92669482-B539-4E4C-BCE3-92829225F5BB");
//		ap.setSv("10.3.3");
//		//ap.setMac(mac);
//		
//		ap.setV("5.50.0");
//		ap.setNw("2g");
//		ap.setIp("10.168.0.10");
//		//ap.setW("320.000000");
//		ap.setScreenHeight(960);
//		ap.setScreenWidth(480);
//		ap.setDeviceType("iPhone5c");
//		ap.setUa("Mozilla/5.0%20(iPhone;%20CPU%20iPhone%20OS%2010_3_3%20like%20Mac%20OS%20X)%20AppleWebKit/603.3.3%20(KHTML,%20like%20Gecko)%20Mobile/14G5037b");
//		ap.setLng(116.403538);
//		ap.setLat(39.994026);
//		//ap.setWifissid("da");
//		//TotalUtil.getYouDaoData(ap, ShowType.OPEN_SCREEN, "1");
//		s.doService(ap, ShowType.OPEN_SCREEN);
		
		
		
		
		
		
		ap.setDpi("2.000000");
		ap.setS("android");
		ap.setImei("866032024542652");
		ap.setUdid("test-bfe5dd89-1a95-4069-bc02-56845a631a2f");
		//ap.setIdfa("F91D631D-B0BF-4F79-9E8E-0331F18E0184");
		//ap.setIdfv("92669482-B539-4E4C-BCE3-92829225F5BB");
		ap.setSv("5.1.1");
		//ap.setMac(mac);
		
		ap.setV("3.52.4_20180605");
		ap.setNw("2g");
		ap.setIp("10.168.0.10");
		//ap.setW("320.000000");
		ap.setScreenHeight(1848);
		ap.setScreenWidth(480);
		ap.setDeviceType("vivo+X7");
		ap.setUa("Mozilla%2F5.0+%28Linux%3B+Android+5.1.1%3B+vivo+X7+Build%2FLMY47V%29+AppleWebKit%2F537.36+%28KHTML%2C+like+Gecko%29+Version%2F4.0+Chrome%2F39.0.0.0+Mobile+Safari%2F537.36");
		ap.setLng(116.403538);
		ap.setLat(39.994026);
		ap.setVendor("vivo");
		//ap.setWifissid("da");
		//TotalUtil.getYouDaoData(ap, ShowType.OPEN_SCREEN, "1");
		s.doService(ap, ShowType.LINE_FEED_ADV);
	}
}
