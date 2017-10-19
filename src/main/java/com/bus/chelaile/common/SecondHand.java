package com.bus.chelaile.common;

import java.net.URLEncoder;

import org.apache.commons.codec.digest.DigestUtils;

import com.bus.chelaile.model.Platform;
import com.bus.chelaile.mvc.AdvParam;


/**
 * 秒针
 * @author zzz
 *
 */
public class SecondHand {
//	private String mo;
//	private String ns;
//	private String m5;
//	private String m0;
//	private String m2;
//	private String m1a;
//	private String m1;
//	private String m6;
//	private String m6a;
//	private String nn;
	
	
	@SuppressWarnings("deprecation")
	public static String replaceSecondUrl(String url,AdvParam ap) {
		
		if( url == null || url.equals("") ){
			return url;
		}
		
		String oa = "0";
		if( ap.getS().equalsIgnoreCase(Platform.IOS.getDisplay()) ){
			oa = "1";
		}
		url = url.replace("__OS__",oa );
		url = url.replace("__IP__",ap.getIp() );
		if( oa.equals("0") ){
			url = url.replace("__IMEI__", DigestUtils.md5Hex(ap.getImei()));
		}else{
			if( iosIsLowerSix(ap.getDeviceType()) ){
				url = url.replace("__IDFA__", ap.getIdfa()+"----");
			}else{
				url = url.replace("__OPENUDID__",ap.getUdid() );
			}	
		}
		url = url.replace("__TERM__",URLEncoder.encode(ap.getDeviceType()));
		url = url.replace("__APP__","chelaile");
		return url;
		
	}
	
	/**
	 * 判断是否是6以下版本
	 * @param deviceType
	 * @return	true 6以下
	 */
	private static boolean iosIsLowerSix(String deviceType){
		if( deviceType.indexOf("iPhone5") != -1 || deviceType.indexOf("iPhone4") != -1 ){
			return true;
		}
		return false;
	}
}
