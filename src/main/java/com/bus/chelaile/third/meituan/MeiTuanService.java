package com.bus.chelaile.third.meituan;

import com.bus.chelaile.common.CacheUtil;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.util.HttpUtils;
import com.bus.chelaile.util.JsonBinder;
import com.bus.chelaile.util.New;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MeiTuanService {

	private static final String url = "http://openapi.meituan.com/poi/search?appkey=139244d8feda47288c25c57746a32ed2550&limit=30&dist=1000&orderby=rating:desc&offset=0&query=美食&deal=true";

	private static final Logger logger = LoggerFactory.getLogger(MeiTuanService.class);

	public static MeituanData getContext(AdvParam ap) {
		
		String key = getCachekey(ap.getLineId(), ap.getStnOrder());
		
		mtContext mc = null;
		
		if( key != null ) {
			Object obj = CacheUtil.get(key);
			if( obj != null ) {
				logger.info("cache={}",(String)obj);
				try {
					mc = JsonBinder.fromJson((String)obj, mtContext.class, JsonBinder.nonNull);
				}catch( Exception e ) {
					logger.error("key=" + key +",value=" + (String)obj + "e:" + e.getMessage(), e);
				}
				
			}
		}
		
		if( mc == null ) {
			mc = getMtContext(ap);
		}
		
		if( mc == null ) {
			return null;
		}

		java.util.Random random = new java.util.Random();// 定义随机类
		int result = random.nextInt(mc.getPoi().size());// 返回[0,10)集合中的整数，注意不包括10

		poiMt mt = mc.getPoi().get(result);

		if (mt == null || mt.getDeals() == null || mt.getDeals().size() == 0) {
			return null;
		}
		result = random.nextInt(mt.getDeals().size());
		dealsMt value = mt.getDeals().get(result);

		MeituanData data = new MeituanData();
		data.setDeepLink(value.getDeaplink_home());
		data.setName(value.getDescription());

		return data;

	}

	private static mtContext getMtContext(AdvParam ap) {
		String queryUrl = url + "&pos=" + ap.getLat() + "," + ap.getLng();

		String entity = null;

		try {
			entity = HttpUtils.get(queryUrl, "utf-8");
		} catch (Exception e) {
			logger.error("udid=" + ap.getUdid() + "e:" + e.getMessage(), e);
			return null;
		}
		if (entity == null) {
			return null;
		}
		mtContext mc = null;
		try {
			mc = JsonBinder.fromJson(entity, mtContext.class, JsonBinder.nonNull);
		} catch (Exception e) {
			logger.error("udid=" + ap.getUdid() + "e:" + e.getMessage(), e);
			return null;
		}

		if (mc.getPoi() == null || mc.getPoi().size() == 0) {
			logger.error("udid=" + ap.getUdid() + ",获取内容为空,code={}", mc.getCode());
			return null;
		}
		
		List<poiMt> pois = New.arrayList();
		
		for( poiMt mt : mc.getPoi() ) {
			if( mt.getDeals() != null && mt.getDeals().size() > 0 ) {
				pois.add(mt);
			}
		}
		
		mc.setPoi(pois);
		
		try {
			String value = JsonBinder.toJson(mc, JsonBinder.nonNull);
			String key = getCachekey(ap.getLineId(), ap.getStnOrder());
			if( key == null ) {
				return mc;
			}
			CacheUtil.setToRedis(key, 3600, value);
		} catch (Exception e) {
			logger.error("udid=" + ap.getUdid() + "e:" + e.getMessage(), e);
			return null;
		}
		return mc;
	}

	private static String getCachekey(String lineId, int stnOrder) {
		if( lineId == null ) {
			return null;
		}
		return "stationMTKey:" + lineId + "," + stnOrder;
	}

	public static void main(String[] args) {
		MeiTuanService mt = new MeiTuanService();
		   CacheUtil.initClient();
		AdvParam ap = new AdvParam();
		ap.setLng(116.43323);
		ap.setLat(39.994337);
		ap.setLineId("2");
		ap.setStnOrder(1);
		mt.getContext(ap);
		// entity =
		// HttpUtils.get("http://openapi.meituan.com/poi/search?appkey=meituan&limit=5&offset=0&pos=39.994337,116.43323&query=洗浴",
		// "utf-8");
		// mt.getContext(null);
	}
}

class mtContext {
	List<poiMt> poi;
	int code;

	public List<poiMt> getPoi() {
		return poi;
	}

	public void setPoi(List<poiMt> poi) {
		this.poi = poi;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

}

class poiMt {
	List<dealsMt> deals;

	public List<dealsMt> getDeals() {
		return deals;
	}

	public void setDeals(List<dealsMt> deals) {
		this.deals = deals;
	}

}

class dealsMt {
	String deaplink_home;
	String description;

	public String getDeaplink_home() {
		return deaplink_home;
	}

	public void setDeaplink_home(String deaplink_home) {
		this.deaplink_home = deaplink_home;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
