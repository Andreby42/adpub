package com.bus.chelaile.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.util.config.PropertiesUtils;

public class UrlUtil {
	
	private static String picUrlPath = 	PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),"innobeOpenPicOriginalPath");
	
	public static Map<String, String> URLRequest(String URL) {
		Map<String, String> mapRequest = new HashMap<String, String>();

		String[] arrSplit = null;

		String strUrlParam = TruncateUrlPage(URL);
		if (strUrlParam == null) {
			return mapRequest;
		}
		// 每个键值为一组 www.2cto.com
		arrSplit = strUrlParam.split("[&]");
		for (String strSplit : arrSplit) {
			String[] arrSplitEqual = null;
			arrSplitEqual = strSplit.split("[=]");

			// 解析出键值
			if (arrSplitEqual.length > 1) {
				// 正确解析
				mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);

			} else {
				if (arrSplitEqual[0] != "") {
					// 只有参数没有值，不加入
					mapRequest.put(arrSplitEqual[0], "");
				}
			}
		}
		return mapRequest;
	}

	private static String TruncateUrlPage(String strURL) {
		String strAllParam = null;
		String[] arrSplit = null;

		strURL = strURL.trim().toLowerCase();

		arrSplit = strURL.split("[?]");
		if (strURL.length() > 1) {
			if (arrSplit.length > 1) {
				if (arrSplit[1] != null) {
					strAllParam = arrSplit[1];
				}
			}
		}

		return strAllParam;
	}
	
	
	public static String getUrlLink(String strURL) {
		String[] arrSplit = strURL.split("[?]");
		if (strURL.length() > 1) {
			return arrSplit[0];
		}

		return strURL;
	}
	
	/**
	 * 根据图片title生成文件名，如果不存在，那么下载图片，保存到本地
	 * @param picUrl
	 * @param fileName
	 * @return	生成的图片本地地址
	 * @throws Exception
	 */
	public static String saveUrlPic(String picUrl,String fileName) throws Exception {
		
		String name = picUrlPath+fileName+"_Original.jpg";
		
		File file = new File(name);
		
		if( file.exists() ){
			return name;
		}
		
			URL url = new URL(picUrl);
			java.io.BufferedInputStream bis = new BufferedInputStream(
					url.openStream());
			byte[] bytes = new byte[1024000];
			
			OutputStream bos = new FileOutputStream(new File(name));
			int len;
			while ((len = bis.read(bytes)) > 0) {
				bos.write(bytes, 0, len);
			}
			bis.close();
			bos.flush();
			bos.close();
			return name;
		
	}
	
	public static void main(String[] args) {
		Map<String, String> map = URLRequest("http://redirect.chelaile.net.cn/?link=http%3A%2F%2Fwww.chelaile.net.cn%2Fweb_active%2Fleifeng%2Fstart.html%3Fwtb%3D1%26wcb%3D1%26utm_source%3Dapp_linedetail%26utm_medium%3Dfloating&wtb=1&wcb=1&utm_source=app_linedetail&utm_medium=floating&adtype=05&advId=2093&utm_medium=floating&line_id=0757-%E6%B2%A507-0&cityId=019&modelVersion=0.0.8&nw=MOBILE_LTE&stats_order=1-1&timestamp=1469140684284&city_id=019&lorder=1&ad_switch=8&geo_type=gcj&deviceType=Coolpad+8732&userId=7869758&platform_v=19&sv=4.4.2&vc=56&stn_order=26&adv_id=2093&beforAds=&stats_referer=lineDetail&utm_source=app_linedetail&geo_lt=5&imei=864974020810926&adv_type=5&udid=d4430c70-1afd-45d8-a04a-9252077566eb&geo_lat=23.109619&push_open=1&v=3.16.0&s=android&stn_name=%E6%B1%9F%E6%B2%B3%E8%B7%AF&last_src=app_chelaile_19&lchsrc=icon&wifi_open=1&geo_lac=29.0&first_src=app_qq_sj&stats_act=auto_refresh&geo_lng=113.163987");
		System.out.println(map.size());
		System.out.println(getUrlLink("http://redirect.chelaile.net.cn/?link=http%3A%2F%2Fwww.chelaile.net.cn%2Fweb_active%2Fleifeng%2Fstart.html%3Fwtb%3D1%26wcb%3D1%26utm_source%3Dapp_linedetail%26utm_medium%3Dfloating&wtb=1&wcb=1&utm_source=app_linedetail&utm_medium=floating&adtype=05&advId=2093&utm_medium=floating&line_id=0757-%E6%B2%A507-0&cityId=019&modelVersion=0.0.8&nw=MOBILE_LTE&stats_order=1-1&timestamp=1469140684284&city_id=019&lorder=1&ad_switch=8&geo_type=gcj&deviceType=Coolpad+8732&userId=7869758&platform_v=19&sv=4.4.2&vc=56&stn_order=26&adv_id=2093&beforAds=&stats_referer=lineDetail&utm_source=app_linedetail&geo_lt=5&imei=864974020810926&adv_type=5&udid=d4430c70-1afd-45d8-a04a-9252077566eb&geo_lat=23.109619&push_open=1&v=3.16.0&s=android&stn_name=%E6%B1%9F%E6%B2%B3%E8%B7%AF&last_src=app_chelaile_19&lchsrc=icon&wifi_open=1&geo_lac=29.0&first_src=app_qq_sj&stats_act=auto_refresh&geo_lng=113.163987"));
		
		String lik = "lineid=%lineId%";
		
		lik = lik.replace("%lineId%", "0290-231");
		System.out.println(lik);
		
	}
}
