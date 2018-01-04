package com.bus.chelaile.flow;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.common.AnalysisLog;
import com.bus.chelaile.model.Platform;
import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.flow.model.ChannelType;
import com.bus.chelaile.flow.model.FlowChannel;
import com.bus.chelaile.flow.model.FlowContent;
import com.bus.chelaile.flow.toutiao.ToutiaoEvent;
import com.bus.chelaile.flow.toutiao.ToutiaoResponse;
import com.bus.chelaile.flow.toutiao.ToutiaoResponseData;
import com.bus.chelaile.util.DateUtil;
import com.bus.chelaile.util.HashTextUtils;
import com.bus.chelaile.util.HttpUtils;
import com.bus.chelaile.util.New;
import com.bus.chelaile.util.config.PropertiesUtils;

public class ToutiaoHelp implements InterfaceFlowHelp{

	@Autowired
	private ActivityService activityService;

	private static final Logger logger = LoggerFactory.getLogger(ToutiaoHelp.class);
	private static final String toutiaoINfoUrl = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			"toutiao.article.url", "http://open.snssdk.com/data/stream/v3/");
	private static final String toutiaoKey = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			"toutiao.secure.key", "1c688e15fc05250cd024030f663fe5bd");
	private static final String toutiaoPartner = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			"toutiao.partner", "chelaile_api");
	private static final String TOUTIAO_NEWS_CLICK_URL = "http://open.snssdk.com/log/app_log_for_partner/v1/";
	private static final String TOUTIAO_ADS_URL = "http://open.snssdk.com/log/app_log_for_partner/v2/";

	@Override
	public List<FlowContent> getInfoByApi(AdvParam advParam, long ftime, String recoid, int id, boolean isShowAd) throws Exception {
		String token = getToutiaoToken(advParam.getUdid());
		if (token == null) {
			logger.error("获取头条token为空! ");
			return null;
		}

		// 读取channelId，调用每个channel
		FlowChannel ucChannel = activityService.getChannels(id, ChannelType.TOUTIAO);
		List<String> apiChannelIds = parseChannel(ucChannel);

		if (id == -1 || apiChannelIds == null || apiChannelIds.size() == 0) {
			logger.info("没有读取到有效的额channelIds，用默认的推荐频道");
			AnalysisLog
					.info("[GET_TOUTIAO_ARTICLES]: accountId={}, udid={}, cityId={}, s={}, v={}, lineId={}, nw={},ip={},deviceType={},geo_lng={},geo_lat={},stats_act={},channelId={},refer={}",
							advParam.getAccountId(), advParam.getUdid(), advParam.getCityId(), advParam.getS(),
							advParam.getV(), advParam.getLineId(), advParam.getNw(), advParam.getIp(),
							advParam.getDeviceType(), advParam.getLng(), advParam.getLat(), advParam.getStatsAct(), 1 ,advParam.getRefer());
			return parseResponse(advParam, ftime, recoid, token, "__all__", isShowAd); // 默认
		} else {

			int contentNumber = 21 / apiChannelIds.size();
			List<FlowContent> UcContents = new ArrayList<>();
			for (String chennelId : apiChannelIds) {
				List<FlowContent> reponseUcContent = parseResponse(advParam, ftime, recoid, token, chennelId, isShowAd);
				if (reponseUcContent != null && reponseUcContent.size() > 0) {
					UcContents.addAll(reponseUcContent.subList(0,
							contentNumber >= reponseUcContent.size() ? reponseUcContent.size() - 1 : contentNumber));
				} else {
					logger.error("出现频道返回为空的情况！ channelId={}", chennelId);
				}
			}
			AnalysisLog
					.info("[GET_TOUTIAO_ARTICLES]: accountId={}, udid={}, cityId={}, s={}, v={}, lineId={}, nw={},ip={},deviceType={},geo_lng={},geo_lat={},stats_act={},channelId={},refer={}",
							advParam.getAccountId(), advParam.getUdid(), advParam.getCityId(), advParam.getS(),
							advParam.getV(), advParam.getLineId(), advParam.getNw(), advParam.getIp(),
							advParam.getDeviceType(), advParam.getLng(), advParam.getLat(), advParam.getStatsAct(), id ,advParam.getRefer());
			return UcContents;

		}
	}

	@Override
	public List<FlowContent> parseResponse(AdvParam advParam, long ftime, String recoid, String token, String channelId, boolean isShowAd) {
		List<FlowContent> contents = New.arrayList();
		String response = getArticles(advParam, ftime, recoid, token, channelId);
		if (response == null) {
			logger.error("头条返回为空: response={}", response);
			return null;
		}
		ToutiaoResponse toutiaoRes = null;
		try{
			toutiaoRes = JSON.parseObject(response, ToutiaoResponse.class);
		}catch(Exception e) {
			logger.error("头条返回结果转json出错，udid={}, response={}", advParam.getUdid(), response);
		}
		if (toutiaoRes == null || toutiaoRes.getRet() != 0 || toutiaoRes.getData() == null) {
			logger.error("头条返回内容异常： udid={}, response= {}", advParam.getUdid(), response);
			return null;
		}
		for (ToutiaoResponseData toutiaoData : toutiaoRes.getData()) {
			if (toutiaoData == null || toutiaoData.isHas_video()) {
				continue;
			}
			FlowContent content = toutiaoData.dealDate(advParam, channelId);
			if (toutiaoData.getLabel() != null && toutiaoData.getLabel().equals("广告")) { // 广告
				content.setDesc("广告");
//				logger.info("上报广告, toutiaoData={}", JSONObject.toJSONString(toutiaoData));
				handleToutiaoShow(toutiaoData, advParam); // 上报 广告 展示事件
				if(! isShowAd) {
					Random r = new Random();
					if(r.nextInt(3) < 2) {
						continue; //不展示广告
					}
				}
			}
			if (content != null && content.getImgs() != null && content.getImgs().size() > 0) { // 不允许没有图片的文章结构
				contents.add(content);
			}
		}
		
		Collections.sort(contents, new Comparator<FlowContent>() {	//排序，按照文章发布时间
			@Override
			public int compare(FlowContent o1, FlowContent o2) {
				if (o1 == null)
					return -1;
				if (o2 == null)
					return 1;

				return (int) (o2.getTime() - o1.getTime());
			}
		});
		
		return contents;
	}

	/*
	 * 获取个性化文章
	 */
	private String getArticles(AdvParam advParam, long ftime, String recoid, String token, String channelId) {
		String url = toutiaoINfoUrl;

		List<NameValuePair> pairs = New.arrayList();
		init(pairs, advParam.getUdid());
		// 通用参数
		pairs.add(new BasicNameValuePair("access_token", token));

		String category = channelId;
		// 关于文章时间
		String min_behot_time = System.currentTimeMillis() / 1000 - 10 + "";
		if ((StringUtils.isEmpty(advParam.getStatsAct()) || !advParam.getStatsAct().equals("get_more")) && ftime != -1L
				&& ftime != 0L) {
			min_behot_time = String.valueOf(ftime);
		}
		String max_behot_time = String.valueOf(ftime / 1000L);

		// 关于本地频道的城市问题
		// String city = "北京";

		// 其他参数
		String ac = "WIFI";// advParam.getNw();
		String language = "simplified";
		pairs.add(new BasicNameValuePair("category", category));
		if (StringUtils.isEmpty(advParam.getStatsAct()) || !advParam.getStatsAct().equals("get_more")) {
			pairs.add(new BasicNameValuePair("min_behot_time", min_behot_time)); // refresh时必填
		}
		if (StringUtils.isNoneEmpty(advParam.getStatsAct()) && advParam.getStatsAct().equals("get_more")
				&& ftime != -1L && ftime != 0L) {
//			logger.info("max_behot_time取传入的ftime值,max_behot_time={}", max_behot_time);
			pairs.add(new BasicNameValuePair("max_behot_time", max_behot_time));
		}
		// //getmore时必填
		pairs.add(new BasicNameValuePair("ac", ac));
		// pairs.add(new BasicNameValuePair("callback", callback)); //跨域jsonp使用
		// pairs.add(new BasicNameValuePair("city", city)); //本地频道时使用
		// pairs.add(new BasicNameValuePair("recent_apps", recent_apps)); //
		// 用户设备上安装的app列表，格式为recent_apps=["a","b"]
		pairs.add(new BasicNameValuePair("language", language));

		String response = null;
		try {
			response = HttpUtils.post(url, pairs, "utf-8");
		} catch (Exception e) {
			logger.error("头条接口获取文章失败：url={}, pairs={}, response={}", url, JSONObject.toJSONString(pairs), response);
		}
		return response;
	}

	/*
	 * 签名
	 */
	private String getSignature(long timestamp, long nonce) {
		String secure_key = toutiaoKey;

		ArrayList<String> list = new ArrayList<String>();
		list.add(secure_key);
		list.add(String.valueOf(nonce));
		list.add(String.valueOf(timestamp));
		Collections.sort(list);
		String signature = HashTextUtils.sha1(list.get(0) + list.get(1) + list.get(2));

		return signature;
	}

	/*
	 * token
	 */
	private String getToutiaoToken(String udid) throws Exception {
		String url = "http://open.snssdk.com/auth/access/web/";
		List<NameValuePair> pairs = New.arrayList();
		init(pairs, udid);

		String response = null;
		String token = null;
		try {
			response = HttpUtils.post(url, pairs, "utf-8");
			token = JSONObject.parseObject(response).getJSONObject("data").getString("access_token");
		} catch (Exception e) {
			logger.error("头条获取access_token失败, url={}, pairs={}, response={}", url, JSONObject.toJSONString(pairs),
					response);
			throw new IllegalArgumentException("toutiao token is null");
		}
		// TODO 存储起来
		// 鉴于目前量不大，可以考虑暂时不予存储，每次需要的时候调用接口获取,观察速度
		return token;
	}

	/*
	 * 5个通用参数, 第三个决定是否获取token， token 单独赋值
	 */
	private void init(List<NameValuePair> pairs, String udid) {
		String partner = toutiaoPartner;
		long timestamp = System.currentTimeMillis();
		long nonce = (long) ((Math.random() * 9 + 1) * 100000);

		String signature = getSignature(timestamp, nonce);

		pairs.add(new BasicNameValuePair("uuid", udid));
		pairs.add(new BasicNameValuePair("timestamp", String.valueOf(timestamp)));
		pairs.add(new BasicNameValuePair("partner", partner));
		pairs.add(new BasicNameValuePair("nonce", String.valueOf(nonce)));
		pairs.add(new BasicNameValuePair("signature", signature));
	}

	@Override
	public List<String> parseChannel(FlowChannel ucChannel) {
		if (ucChannel == null) {
			return null;
		}
		List<String> ids = new ArrayList<String>();
		for (String id : ucChannel.getChannelId().split("&")) {
			ids.add(id);
		}
		return ids;
	}

	/**
	 * 解析从 kafka 读取到的日志，上报点击事件
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public void handleToutiaoClick(String line) {
		try {
			String[] segs = line.split("\\|#");
			String content = segs[3].trim();
			String time = segs[2].trim();
			String ip = segs[0].trim().split("nginx:")[1].trim();
			int beginIdx = content.indexOf("=");
			int endIdx = content.lastIndexOf(" ");
			String encodedURL = content.substring(beginIdx + 1, endIdx);

			String tag = null;
			String url = null;
			String response = null;
			Map<String, String> params = paramsAnalysis(encodedURL);
			if (params != null) {
				tag = params.get("tag");
				if (tag == null) {
					logger.error("头条点击url没有tag值: line={}", line);
					return;
				}
				String udid = params.get("udid");
				List<NameValuePair> pairs = New.arrayList();
				init(pairs, udid);
				pairs.add(new BasicNameValuePair("access_token", getToutiaoToken(udid)));

				String ua = params.get("ua");
				String pdid = params.get("pdid");
				String device_type = getDeviceType(params.get("device_type"));

				String category = params.get("category");
				String label = params.get("label");
				long value = Long.parseLong(params.get("value"));
				String is_ad_event = params.get("is_ad_event");
				long nt = getNetType(params.get("nt"));
				String log_extra = params.get("log_extra");
				long client_at = DateUtil.getDate(time, "yyyy-MM-dd HH:mm:ss").getTime() / 1000;
				long show_time = new Random().nextInt(10); // TODO　获取展示广告到点击广告之间的时间差(暂时虚拟)

				List<ToutiaoEvent> events = New.arrayList();
				if (tag.equals("embeded_ad")) { // 广告点击
					url = TOUTIAO_ADS_URL;
					pairs.add(new BasicNameValuePair("ua", ua));
					pairs.add(new BasicNameValuePair("pdid", pdid));
					pairs.add(new BasicNameValuePair("device_type", device_type));
					ToutiaoEvent event = new ToutiaoEvent(category, tag, is_ad_event, label, value, log_extra, nt,
							client_at, show_time, ip);
					events.add(event);
					pairs.add(new BasicNameValuePair("events", JSONObject.toJSONString(events)));
				} else { // 文章点击
					url = TOUTIAO_NEWS_CLICK_URL;
					ToutiaoEvent event = new ToutiaoEvent(category, tag, label, value, time);
					events.add(event);
					pairs.add(new BasicNameValuePair("events", JSONObject.toJSONString(events)));
				}

				try {
					response = HttpUtils.post(url, pairs, "utf-8");
					logger.info("头条广告点击上报成功：url={}, pairs={}, response={}", url, JSONObject.toJSONString(pairs), response);
				} catch (Exception e) {
					logger.error("头条点击上报失败：url={}, pairs={}, response={}", url, JSONObject.toJSONString(pairs),
							response);
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			logger.error("解析头条点击日志错误：line={}", line);
			e.printStackTrace();
		}

	}

	/*
	 * 上报 头条广告展示
	 */
	public void handleToutiaoShow(ToutiaoResponseData data, AdvParam advParam) {
		try {
			List<NameValuePair> pairs = New.arrayList();
			String udid = advParam.getUdid();
			init(pairs, udid);
			pairs.add(new BasicNameValuePair("access_token", getToutiaoToken(udid)));

			String ua = advParam.getUa();
			String pdid = advParam.getUdid();
			Platform platform = Platform.from(advParam.getS());
			if (platform.isIOS(advParam.getS())) {
				pdid = advParam.getIdfa();
			}
			String device_type = getDeviceType(advParam.getDeviceType());

			String tag = data.getTag();
			String category = "open";
			String label = "show";
			long value = data.getAd_id();
			String is_ad_event = "1";
			long nt = getNetType(advParam.getNw());
			String log_extra = data.getLog_extra();
			long client_at = new Date().getTime() / 1000;
			long show_time = 0l; // show这个字段应该不是必须的
			String ip = advParam.getIp();

			List<ToutiaoEvent> events = New.arrayList();
			pairs.add(new BasicNameValuePair("ua", ua));
			pairs.add(new BasicNameValuePair("pdid", pdid));
			pairs.add(new BasicNameValuePair("device_type", device_type));
			ToutiaoEvent event = new ToutiaoEvent(category, tag, is_ad_event, label, value, log_extra, nt, client_at,
					show_time, ip);
			events.add(event);
			pairs.add(new BasicNameValuePair("events", JSONObject.toJSONString(events)));

			String url = null;
			String response = null;
			url = TOUTIAO_ADS_URL;
			try {
				response = HttpUtils.post(url, pairs, "utf-8");
//				logger.info("头条广告展示上报成功：url={}, pairs={}, response={}", url, JSONObject.toJSONString(pairs), response);
			} catch (Exception e) {
				logger.error("头条广告展示上报失败：url={}, pairs={}, response={}", url, JSONObject.toJSONString(pairs), response);
				e.printStackTrace();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}
	}

	/*
	 * 根据nw值转换得到nt 未知：0，2G:1，3G:2，4G:3，wifi:4
	 */
	private long getNetType(String nw) {
		if (nw == null || nw.contains("UNKNOWN"))
			return 0;
		if (nw.contains("4G") || nw.contains("MOBILE_LTE")) {
			return 3;
		}
		if (nw.contains("WIFI") || nw.contains("WiFi")) {
			return 4;
		}
		if (nw.contains("2G")) {
			return 1;
		}
		return 2; // 其他的都是3G
	}

	private String getDeviceType(String deviceType) {
		if (deviceType == null)
			return "andriod"; // 默认android
		if (deviceType.contains("iPhone")) {
			return "iphone";
		} else if (deviceType.contains("iPad")) {
			return "ipad";
		} else {
			return "andriod";
		}
	}

	private Map<String, String> paramsAnalysis(String url) {
		Map<String, String> params = New.hashMap();
		String entrys[] = url.split("&");
		for (String s : entrys) {
			String[] maps = s.split("=");
			try {
				if (maps != null && maps.length >= 2)
					params.put(maps[0], URLDecoder.decode(maps[1], "UTF-8"));
			} catch (Exception e) {
				logger.error("参数解析出错: map={}", maps.toString());
				e.printStackTrace();
			}
		}
		return params;
	}

	public static void main(String[] args) {
		ToutiaoHelp toutiaoHelp = new ToutiaoHelp();
		// AdvParam advParam = new AdvParam();
		// advParam.setUdid("aaa");
		// long ftime = System.currentTimeMillis();
		// String recoid = "1241231254123";
		// String token = toutiaoHelp.getToutiaoToken(advParam.getUdid());
		// String channelId = "news_car";
		// String response = toutiaoHelp.getArticles(advParam, ftime, recoid,
		// token, channelId);
		// if (response == null) {
		// return;
		// }
		//
		// ToutiaoResponse toutiaoRes = JSON.parseObject(response,
		// ToutiaoResponse.class);
		// System.out.println(JSONObject.toJSONString(toutiaoRes.getData()));
		//
		// List<UcContent> contents = New.arrayList();
		// ToutiaoResponse toutiaoRes1 = JSON.parseObject(response,
		// ToutiaoResponse.class);
		// if (toutiaoRes1.getRet() != 0 || toutiaoRes1.getData() == null) {
		// return;
		// }
		//
		// for (ToutiaoResponseData toutiaoData : toutiaoRes1.getData()) {
		// UcContent content = toutiaoData.dealDate(advParam, channelId);
		// if (content.getImgs() != null) { // 不允许没有图片的文章结构
		// contents.add(toutiaoData.dealDate(advParam, channelId));
		// }
		// }
		//
		// System.out.println(JSONObject.toJSONString(contents));

//		String url = "<190>Apr  6 17:45:40 web7 nginx: 117.136.79.227 |# - |# 2017-04-06 17:45:40 |# GET /?link=https%3A%2F%2Fopen.toutiao.com%2Fa6405467749933383937%2F%3Futm_campaign%3Dopen%26utm_medium%3Dapi%26utm_source%3Dtt%26ad_id%3D58857685260%26cid%3D58858128136%26req_id%3D1491472086853814819%26linkRefer%3Ddirect&log_extra=%7B%22rit%22%3A+1%2C+%22convert_id%22%3A+0%2C+%22req_id%22%3A+%221491472086853814819%22%2C+%22ad_price%22%3A+%22WOYOCgAL2i9Y5g4KAAvaL4ufE0FHPUDeSyQnIA%22%7D&nt=0&category=open&tag=go_detail&value=6405467749933383937&label=click&udid=aaa&is_ad_event=1 HTTP/1.1 |# 302 |# 0.000 |# 264 |# - |# Mozilla/5.0 (iPhone; CPU iPhone OS 10_2_1 like Mac OS X) AppleWebKit/602.4.6 (KHTML, like Gecko) Mobile/14D27 Chelaile/5.20.0 |# - |# ad.chelaile.net.cn |# - |# -";
//		toutiaoHelp.handleToutiaoClick(url);
//		// System.exit(0);
//
//		Map<String, String> map = New.hashMap();
//		String tag = "11";
//		map.put("11", tag);
//		tag = "22";
//		map.put("11", tag);
//		System.out.println(map.get("11"));
		
		
		for(int i = 0; i < 30; i ++ ) {
			Random r = new Random();
			System.out.println(r.nextInt(3));
		}
		
		
	}
}
