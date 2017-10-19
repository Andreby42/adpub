package com.bus.chelaile.common;
import java.net.URLEncoder;

import org.apache.commons.codec.digest.DigestUtils;

import com.bus.chelaile.model.Platform;
import com.bus.chelaile.mvc.AdvParam;



public class AdMaster {
//	/**
//	 * 0 android 
//	 * 1 ios
//	 */
//	private String Oa;	// OS
//	/**
//	 * 取md5摘要
//	 */
//	private String Oc;  // IMEI
//	/**
//	 * 去除分隔符":",取md5摘要
//	 */
//	private String z;	// IDFA
//	private String f;	// IP	
//	/**
//	 * 用户终端机型
//	 */
//	private String r;	// TERM
	
	
	@SuppressWarnings("deprecation")
	public static String adMasterReplace(String url,AdvParam ap){
		
		if( url == null || url.equals("") ){
			return url;
		}
		
		String oa = "0";
		if( ap.getS().equalsIgnoreCase(Platform.IOS.getDisplay()) ){
			oa = "1";
		}
		url = url.replace("__OS__",oa );
		if( oa.equals("0") ){
			url = url.replace("__IMEI__", DigestUtils.md5Hex(ap.getImei()));
		}else{
			url = url.replace("__IDFA__",DigestUtils.md5Hex(ap.getIdfa()) );
		}
		
		url = url.replace("__IP__",ap.getIp() );
		url = url.replace("__TERM__",URLEncoder.encode(ap.getDeviceType()));
		return url;
	}
	
	
	
}
