package com.bus.chelaile.push;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.model.Platform;
import com.bus.chelaile.model.PushType;
import com.bus.chelaile.push.pushModel.SinglePushParam;
import com.bus.chelaile.util.AdvUtil;
import com.bus.chelaile.util.OnsUtil;

public class SinglePushService extends AbstractPushManager {

	protected static final Logger logger = LoggerFactory.getLogger(SinglePushService.class);
	
	/*
	 * 对单个用户推送
	 */
	public boolean noticePushBySingleUdid(SinglePushParam singlePushParam, String udid) {
		Calendar now = Calendar.getInstance();
		if (singlePushParam.getPushType().equalsIgnoreCase(PushType.FEED.getType())) {
			now.add(Calendar.HOUR_OF_DAY, 1);
		} else if (singlePushParam.getPushType().equalsIgnoreCase(PushType.FEEDBACK.getType())) {
			now.add(Calendar.DAY_OF_YEAR, 3);
		} else {
			logger.error("noticePushByUdid {}, type {} is wrong", udid, singlePushParam.getPushType());
			return false;
		}
		singlePushParam.setEndDate(now.getTime());
		sendPushNoticeBySingleUDID(singlePushParam, udid);
		return true;
	}

	public void sendPushNoticeBySingleUDID(SinglePushParam singlePushParam, String udid) {
		long st = System.currentTimeMillis();
		List<String> udidList = new ArrayList<String>();
		udidList.add(udid);
		Map<String, String> tokenMap = getTokenByUdidsFromOcs(udidList);
		logger.info("SingleUDID udid:{}  Tokens get from ocs: {} costs: {}", udid, tokenMap.size(),
				(System.currentTimeMillis() - st));
		String tokenStr = tokenMap.get(udid);
		if (StringUtils.isBlank(tokenStr)) { // ocs中没有 从db中取
			/*
			 * ReminderUdidTokenElement tokenElement =
			 * tokenService.getTokenFromDB(udid); if (null == tokenElement) {
			 * logger.info("SingleUDID udid:{}, not find token", udid); return;
			 * } token = tokenElement.getToken(); if
			 * (tokenElement.getS().equalsIgnoreCase(IOS.getValue())) { token =
			 * tokenElement.getToken(); Map<String, IOSDelToken> map =
			 * tokenService.getIOSDelTokensMap(); if (map.containsKey(token)) {
			 * logger.info("SingleUDID udid:{}, token is not valid", udid);
			 * return; } }
			 */
			logger.info("SingleUDID udid:{}, not find token", udid);
			return;
		}
		String[] tokens = tokenStr.split("\\|");
		String token = tokens[0];
		String platform = getPlatform(udid, tokenStr);
		logger.info("SingleUDID token: {} platform: {}, udid:{}", tokenStr, platform, udid);
		logger.info("[{}Push-All] udid:{}, pushKey:{}, title:{}", singlePushParam.getPushType(), udid,
				singlePushParam.getPushKey(), singlePushParam.getTitle());
		if (Platform.IOS.getValue().equals(platform)) {
			this.sendNotice(token, udid, singlePushParam, Platform.IOS);
		} else if (Platform.IOSJG.getValue().equals(platform)) {
			this.sendNotice(token, udid, singlePushParam, Platform.IOSJG);
		} else if (Platform.GT.getValue().equals(platform)) {
			this.sendNotice(token, udid, singlePushParam, Platform.GT);
		} else if (Platform.YM.getValue().equals(platform)) {
			this.sendNotice(token, udid, singlePushParam, Platform.YM);
		} else if (Platform.JG.getValue().equals(platform)) {
			this.sendNotice(token, udid, singlePushParam, Platform.JG);
		}
	}

	public void sendNotice(String token, String udid, SinglePushParam singlePushParam, Platform platform) {
		long st = System.currentTimeMillis();
		String expireTime = buildExpireTime(singlePushParam.getEndDate(), platform);
		logger.info("[{}Push-{}Token] udid:{}, pushKey:{}, title:{}, s:{}", singlePushParam.getPushType(),
				platform.getValue(), udid, singlePushParam.getPushKey(), singlePushParam.getTitle(),
				platform.getValue());
		JSONObject sendMsg = new JSONObject();
		sendMsg.put("expiredTime", expireTime);
		sendMsg.put("drives", platform.getValue());
		if (platform == Platform.YM) {
			sendMsg.put("serialNumber", PushSerialNumberCreateUtil.getSerialNumber());
		}
		List<Object> bodyObjects = buildNoticeMsgBody(singlePushParam, platform, udid);
		String msgBody = (String) bodyObjects.get(0);
		int bodyLength = (int) bodyObjects.get(1);
		sendMsg.put("msgBody", msgBody);
		List<String> tokenList = new ArrayList<>();
		tokenList.add(token + "#" + udid);
		List<Object> objects = AdvUtil.toJson(tokenList, 0, 1, bodyLength);
		JSONArray clientList = (JSONArray) objects.get(0);
		int passMaxClient = (int) objects.get(1);
		sendMsg.put("clients", clientList);
		logger.info("Generated Msg: msg={}, passMaxLengthSize={} costs {} ms, udid={}", sendMsg.toJSONString(), bodyLength,
				(System.currentTimeMillis() - st), udid);
		if (passMaxClient == 1) {
			logger.info("udid {}, token {}, pushType:{} platform={}, 超过220长度数={}, costs {} ms", udid, token,
					singlePushParam.getPushType(), platform, passMaxClient, (System.currentTimeMillis() - st));
			return;
		}
		String res = OnsUtil.send(PUSH_KEY, sendMsg.toJSONString());
		if (res.equalsIgnoreCase("-1")) {
			logger.error("[ONS_SEND_FAIL] 发送消息到ONS队列之中失败: udid {}, token {}, res={}, costs {} ms", udid, token, res,
					(System.currentTimeMillis() - st));
		} else {
			saveTokenUdidToOcs(tokenList, res);
			logger.info("[{}Push-{}Push] udid:{}, pushKey:{}, title:{}, s:{}", singlePushParam.getPushType(),
					platform.getValue(), StringUtils.substringAfter(tokenList.get(0), "#"),
					singlePushParam.getPushKey(), singlePushParam.getTitle(), platform.getValue());
		}
		logger.info("完成发送广告： udid {}, token {}, platform={}, , msgId={}, 发送的token数={}, 超过220长度数={}, costs {} ms", udid,
				token, platform, res, tokenList.size(), passMaxClient, (System.currentTimeMillis() - st));
	}
	
	private List<Object> buildNoticeMsgBody(SinglePushParam singlePushParam, Platform platform, String udid) {
		String content = singlePushParam.getBody();
		if (singlePushParam.getPushType().equalsIgnoreCase(PushType.FEEDBACK.getType()) && ((Platform.IOS == platform) || Platform.IOSJG == platform)) {
			content = singlePushParam.getTitle();
		}
		String title = singlePushParam.getTitle();
		String type = "2";
		String pushKey = singlePushParam.getPushKey();
		String tranferdUrl = AdvUtil.buildRedirectLink(singlePushParam.getLink(), PUSH_NOTICE_PARAM_MAP, udid, true,
				false, 1);
		return buildMsgJSonString(type, title, content, pushKey, tranferdUrl, platform, 0);
	}
}
