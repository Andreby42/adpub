package com.bus.chelaile.push;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.model.Platform;
import com.bus.chelaile.push.pushModel.ParentPush;
import com.bus.chelaile.push.pushModel.PushMessageBody;
import com.bus.chelaile.push.pushModel.SinglePushParam;
import com.bus.chelaile.util.JsonBinder;
import com.bus.chelaile.util.New;
import com.bus.chelaile.util.OnsUtil;

public class NoticePushService extends AbstractPushManager {

	// 话题回复
	private final static int FEED_INFO = 5;
	private static final Logger logger = LoggerFactory.getLogger(NoticePushService.class);
	
	/**
	 * 话题推送
	 * 
	 * @param singlePushParam
	 * @param udid
	 * @param badge
	 * @return
	 * @throws Exception
	 */
	public boolean noticePushFeedInfo(SinglePushParam singlePushParam, String udid, String badge) throws Exception {
		int badgeNum = Integer.parseInt(badge);
		long st = System.currentTimeMillis();

		List<String> udidList = new ArrayList<String>();
		udidList.add(udid);
		Map<String, String> tokenMap = getTokenByUdidsFromOcs(udidList);
		String tokenStr = tokenMap.get(udid);
		if (StringUtils.isBlank(tokenStr)) {
			logger.info("SingleUDID udid:{}, not find token", udid);
			return true;
		}
		logger.info("tokenStr=" + tokenStr);
		String[] tokens = tokenStr.split("\\|");
		String token = tokens[0];
		List<String> tokenList = New.arrayList();
		tokenList.add(token);

		String platform = getPlatform(udid, tokenStr);
		// 推送message
		JSONObject jsonObject = getPushFeedInfoMsg(singlePushParam, platform, FEED_INFO);
		if (badgeNum > 0) {
			if (platform.equalsIgnoreCase(Platform.IOS.getValue())) {
				jsonObject.put("bd", badgeNum);
			} else {
				jsonObject.put("badge", badgeNum);
			}

		}

		ParentPush pt = getParentPush(platform, singlePushParam, tokenList, jsonObject.toString());
		pt.setMsgBody(jsonObject.toString());

		String json = JsonBinder.toJson(pt, JsonBinder.nonNull);

		logger.info("sendJson:" + json);

		String res = OnsUtil.send("feedInfo_push", json);

		return writeSendResult(res, udid, token, st, singlePushParam, platform, json);
	}
	
	
	/**
	 * 话题回复
	 * 
	 * @param singlePushParam
	 * @param platform
	 * @return
	 */
	private JSONObject getPushFeedInfoMsg(SinglePushParam singlePushParam, String platform, int type) {
		if (Platform.IOS.getValue().equals(platform)) {
			return PushMessageBody.getMessageBody(type, singlePushParam.getBody(), singlePushParam.getPushKey(),
					Platform.IOS, "");
		} else if (Platform.IOSJG.getValue().equals(platform)) {
			return PushMessageBody.getMessageBody(type, singlePushParam.getBody(), singlePushParam.getPushKey(),
					Platform.IOSJG, "");
		} else if (Platform.GT.getValue().equals(platform)) {
			return PushMessageBody.getMessageBody(type, singlePushParam.getBody(), singlePushParam.getPushKey(),
					Platform.GT, singlePushParam.getTitle());
		} else if (Platform.YM.getValue().equals(platform)) {
			JSONObject jsonObject = PushMessageBody.getMessageBody(type, singlePushParam.getBody(),
					singlePushParam.getPushKey(), Platform.YM, singlePushParam.getTitle());
			// 友盟需要增加一个序列号
			jsonObject.put("serialNumber", PushSerialNumberCreateUtil.getSerialNumber());
			return jsonObject;
		} else if (Platform.JG.getValue().equals(platform)) {
			return PushMessageBody.getMessageBody(type, singlePushParam.getBody(), singlePushParam.getPushKey(),
					Platform.JG, singlePushParam.getTitle());
		}
		throw new IllegalArgumentException("未找到类型的platform:" + platform);
	}
	
	
	private ParentPush getParentPush(String platform, SinglePushParam singlePushParam, List<String> tokenList,
			String msgBody) {

		Platform pF = getPlatform(platform);

		String expireTime = buildExpireTime(singlePushParam.getEndDate(), pF);

		ParentPush push = new ParentPush();
		push.setClients(tokenList);
		push.setDrives(pF.getValue());
		push.setExpiredTime(expireTime);
		push.setMsgBody(msgBody);
		return push;
	}

	private Platform getPlatform(String platform) {
		if (Platform.IOS.getValue().equals(platform)) {
			return Platform.IOS;
		} else if (Platform.GT.getValue().equals(platform)) {
			return Platform.GT;
		} else if (Platform.YM.getValue().equals(platform)) {
			return Platform.YM;
		} else if (Platform.JG.getValue().equals(platform)) {
			return Platform.JG;
		} else if (Platform.IOSJG.getValue().equals(platform)) {
			return Platform.IOSJG;
		}
		throw new IllegalArgumentException("未找到类型的platform:" + platform);
	}
	
	private boolean writeSendResult(String res, String udid, String token, long st, SinglePushParam singlePushParam,
			String platform, String sendJson) {

		List<String> tokenList = new ArrayList<>();
		tokenList.add(token + "#" + udid);

		if (res.equalsIgnoreCase("-1")) {
			logger.error("[ONS_SEND_FAIL] 发送消息到ONS队列之中失败: udid {}, token {}, res={}, costs {} ms", udid, token, res,
					(System.currentTimeMillis() - st));
			return false;
		} else {
			saveTokenUdidToOcs(tokenList, res);
			logger.info("[{}Push-{}Push] udid:{}, pushKey:{}, title:{}, s:{}", singlePushParam.getPushType(), platform,
					StringUtils.substringAfter(tokenList.get(0), "#"), singlePushParam.getPushKey(),
					singlePushParam.getTitle(), platform);
		}
		logger.info("完成发送广告： udid {}, token {}, platform={}, , msgId={}, 发送的token数={}, 超过220长度数={}, costs {} ms", udid,
				token, platform, res, tokenList.size(), sendJson.length() - "msgBody".length(),
				(System.currentTimeMillis() - st));
		return true;
	}
	
}
