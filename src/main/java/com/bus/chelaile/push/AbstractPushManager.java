package com.bus.chelaile.push;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.common.CacheUtil;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.model.Platform;
import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.push.pushModel.TokenType;
import com.bus.chelaile.push.task.UdidTokenToOcsTask;
import com.bus.chelaile.util.AdvUtil;
import com.bus.chelaile.util.New;
import com.bus.chelaile.util.config.PropertiesUtils;

public class AbstractPushManager {
	
	protected static final String PUSH_KEY = "AdPush";
	private static ExecutorService udidTokenToOcsThreadPool = Executors.newFixedThreadPool(Integer
			.parseInt(PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "udidToken.toOcs.threadcount")));
	protected static final Map<String, String> PUSH_NOTICE_PARAM_MAP = new HashMap<String, String>() {
		private static final long serialVersionUID = 1L;
		{
			put(Constants.PARAM_AD_TYPE, ShowType.PUSH_NOTICE.getType());
		}
	};
	
	private static final Logger logger = LoggerFactory.getLogger(AbstractPushManager.class);
	
	public Map<String, String> getTokenByUdidsFromOcs(List<String> udidList) {
		long st = System.currentTimeMillis();
		Map<String, String> resultMap = New.hashMap();
		final int maxRecord = Integer.parseInt(PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
				"max.per.redis.request"));

		final int sleepMSecond = Integer.parseInt(PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
				"max.per.redis.sleepMSecond"));
		int count = 0;
		List<String> tempList = New.arrayList();
		int listSize = udidList.size();
		int queryTimes = 0;
		for (int idx = 0; idx < listSize; idx++) {
			String udidStr = udidList.get(idx);
			if (udidStr.startsWith("REMINDERTOKEN#")) {
				tempList.add(udidStr);
			} else {
				String udidCacheKey = "REMINDERTOKEN#" + udidStr;
				tempList.add(udidCacheKey);
			}
			count++;
			if (count >= maxRecord || idx == listSize - 1) {
				try {
					queryTimes++;
					final int size = tempList.size();
					Map<String, Object> mapFromOcs = CacheUtil.getByList(tempList);
					logger.info("getTokenByUdids 从ocs之中获得TOKEN: queryTime= " + queryTimes + ", count=" + size
							+ ", totalToken=" + mapFromOcs.size());
					if (mapFromOcs != null) {
						for (String key : mapFromOcs.keySet()) {
							if (null != mapFromOcs.get(key)) {
								resultMap.put(key.replaceFirst("REMINDERTOKEN#", ""), (String) mapFromOcs.get(key));
							}
						}
					}
					Thread.sleep(sleepMSecond);
				} catch (Exception ex) {
					logger.error("getTokenByUdids ocs中查询TOKEN异常: " + ex.getMessage(), ex);
				} finally {
					count = 0;
					tempList.clear();
				}
			}
		}
		logger.info("getTokenByUdids from ocs query {} token costs {} ms,reultMap.size={}", udidList.size(),
				(System.currentTimeMillis() - st), resultMap.size());
		return resultMap;
	}
	
	/**
	 * 根据 token的样式，选择通过何种推送渠道
	 * @param udid
	 * @param tokenStr
	 * @return Platform.getValue()
	 */
	public String getPlatform(String udid, String tokenStr) {
		// token|ios|tokenType token|android|tokenType
		String platform = "";
		String[] tokens = tokenStr.split("\\|");
		logger.info("tokens size " + tokens.length);
		String token = tokens[0].trim();
		if (AdvUtil.isMessyCode(token)) {
			logger.info("getPlatform udid:{}, token {} is not valid", udid, tokenStr);
		}
		if (tokens.length == 1) {
			platform = token.length() == 64 ? Platform.IOS.getValue() : Platform.GT.getValue();
		} else if (tokens.length == 3) {
			platform = tokens[1].trim();    // 去第二个部分
			logger.info("platForm " + platform);
			 // android 的platform修改
			String tokenType = tokens[2].trim();
			if (Platform.ANDROID.getValue().equalsIgnoreCase(platform)) {
				logger.info("tokeyTyp: " + tokenType);
				if (TokenType.GTTOKEN.getName().equalsIgnoreCase(tokenType)) {
					platform = TokenType.GTTOKEN.getPlatform().getValue();
				} else if (TokenType.YMTOKEN.getName().equalsIgnoreCase(tokenType)) {
					platform = TokenType.YMTOKEN.getPlatform().getValue();
				} else if(TokenType.JGTOKEN.getName().equalsIgnoreCase(tokenType)) {
					platform = TokenType.JGTOKEN.getPlatform().getValue();
				}
			}
			//  ios 的platform修改
			else if (Platform.IOS.getValue().equalsIgnoreCase(platform) && 
					TokenType.IOSJGTOKEN.getName().equalsIgnoreCase(tokenType)) {
				platform = TokenType.IOSJGTOKEN.getPlatform().getValue();
			}
		}
		return platform;
	}

	
	protected String buildExpireTime(Date endDate, Platform platform) {
		SimpleDateFormat dFormat = new SimpleDateFormat("yyyyMMdd");
		Date dateToUse = endDate;
		if (endDate == null) {
			dateToUse = AdvUtil.getNDaysAfter(2);
		} else if (platform == Platform.YM) {
			Date after7 = AdvUtil.getNDaysAfter(5);
			String after7Str = dFormat.format(after7);
			String dateStr = dFormat.format(dateToUse);
			if (after7Str.compareTo(dateStr) < 0) {
				dateToUse = after7;
			}
		}
		String expireStr = dFormat.format(dateToUse) + "233059";
		return expireStr;
	}
	
	
	/**
	 * 保存token与udid到ocs中
	 * 
	 * @param tokenList
	 *            token与udid集合
	 * @param res
	 *            资源
	 */
	protected void saveTokenUdidToOcs(List<String> tokenList, String res) {
		udidTokenToOcsThreadPool.execute(new UdidTokenToOcsTask(res, tokenList, 7 * 24 * 60 * 60));
	}
	
	protected List<Object> buildMsgJSonString(String type, String title, String content, String pushKey, String link,
			Platform platform, int openType) {
		List<Object> resultList = new ArrayList<>();
		if (content == null) {
			resultList.add("");
			resultList.add(Constants.PUSH_BODYLENGTH_DEFAULT);
			logger.error("SinglePushParam body is NULL.");
			return resultList;
		}
		try {
			/**
			 * 所有的IOS都采用新的消息格式。
			 */
			JSONObject jsonObject = new JSONObject();
			if (platform == Platform.IOS || platform == Platform.IOSJG) {
				// 使用新版本的消息推送。
				// jsonObject.put("sound", "default");
				jsonObject.put("loc-key", content);
				jsonObject.put("a", pushKey);
				jsonObject.put("e", link);
				jsonObject.put("type", Integer.parseInt(type));
				// 话题需要增加子类型
				if (type.equals("5")) {
					jsonObject.put("st", 1);
				}
				int bodyLength = jsonObject.toString().getBytes("UTF-8").length;
				logger.info("bodyLenght: {}", bodyLength);
				resultList.add(jsonObject.toString());
				resultList.add(bodyLength);
				return resultList;
			}
			// 话题需要增加子类型
			if (type.equals("5")) {
				jsonObject.put("subtype", 1);
			}
			jsonObject.put("type", Integer.parseInt(type));
			jsonObject.put("title", title);
			jsonObject.put("message", content);
			jsonObject.put("push_key", pushKey);
			jsonObject.put("open_type", openType);
			jsonObject.put("link", link);
			resultList.add(jsonObject.toString());
			resultList.add(Constants.PUSH_BODYLENGTH_DEFAULT);
		} catch (Exception e) {
			logger.error("buildMsgJSonString exception", e);
		}
		return resultList;
	}
}
