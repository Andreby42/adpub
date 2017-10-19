package com.bus.chelaile.push.task;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.common.TimeLong;
import com.bus.chelaile.model.Platform;
import com.bus.chelaile.model.ads.AdContent;
import com.bus.chelaile.model.rule.Rule;
import com.bus.chelaile.push.AdsPushService;
import com.bus.chelaile.util.New;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/1/25 0025.
 */
public class PushRequestTask implements Runnable {

	private AdContent adv;
	private List<String> udidList;
	private Rule rule;
	private AdsPushService adsPushService;
	private boolean isInside;

	private Date endDate;
	// 保存已经发送完成的token
	private static Map<String, Integer> tokenSendMap = New.concurentMap();
	
	private static final Logger logger = LoggerFactory.getLogger(PushRequestTask.class);

	// private static final ExecutorService exec =
	// Executors.newFixedThreadPool(10);

	public PushRequestTask(AdContent adv, List<String> udidList, Rule rule,
			AdsPushService pushService, Date endDate, boolean isInside) {
		this.adsPushService = pushService;
		this.adv = adv;
		this.udidList = udidList;
		this.rule = rule;
		this.endDate = endDate;
		this.isInside = isInside;
	}

	@Override
	public void run() {
		TimeLong.info("PushRequestTask into run adv={} rule={},size={}",
				adv.getId(), rule.getRuleId(), udidList.size());

		long st = System.currentTimeMillis();
		try {
			// 需要注意，如果中断了某次的推送，后面如何能够继续？已经发送了的udid是否能够不再进行发送？
			List<String> andriodGeTuiTokenList = new ArrayList<String>();
			List<String> andriodYouMengTokenList = new ArrayList<String>();
			List<String> iosTokenList = new ArrayList<String>();
			Map<String, String> tokenMap = adsPushService
					.getTokenByUdidsFromOcs(udidList);
			TimeLong.info(
					"PushRequestTask Tokens get from ocs: {} costs: {}, adv={}, rule={},udidSize={}",
					tokenMap.size(), (System.currentTimeMillis() - st),
					adv.getId(), rule.getRuleId(), udidList.size());
			String platform = null;
			String token =null;
			for (String udid : tokenMap.keySet()) {
				String tokenStr = tokenMap.get(udid);
				if(tokenStr == null ) {
					continue;
				}
				try{
					String[] tokens = tokenStr.split("\\|");
					token= tokens[0];
					platform = adsPushService.getPlatform(udid, tokenStr);
					if (StringUtils.isBlank(platform)) {
						TimeLong.info(
								"PushRequestTask udid:{}, token {} is not valid,adv={}",
								udid, tokenStr, adv.getId());
						continue;
					}
				}catch( Exception e ){
					logger.error(e.getMessage(),e);
					continue;
				}
				

				if (tokenSendMap.containsKey(tokenStr)) {
					int advId = tokenSendMap.get(tokenStr);
					if (advId == adv.getId()) {
						TimeLong.info("udid={},token={},有重复", udid, token);
						if(! isInside) {	//如果不是内推，那么不再推送
							continue;
						}
					}

				} else {
					if(! isInside) {	//如果不是内推，那么放入缓存中，后续不再推送
						tokenSendMap.put(tokenStr, adv.getId());
					} else {
						TimeLong.info("内推记录：udid={},token={}", udid, token);
					}
				}

				if (Platform.IOS.getValue().equals(platform)) {
					iosTokenList.add(token + "#" + udid);
				} else if (Platform.GT.getValue().equals(platform)) {
					andriodGeTuiTokenList.add(token + "#" + udid);
				} else if (Platform.YM.getValue().equals(platform)) {
					andriodYouMengTokenList.add(token + "#" + udid);
				}

			}

			TimeLong.info("PushRequestTask  All andriodGT tokens: {},adv={}",
					andriodGeTuiTokenList.size(), adv.getId());
			TimeLong.info("PushRequestTask All IOS tokens: {},adv={}",
					iosTokenList.size(), adv.getId());
			TimeLong.info("PushRequestTask All andriodYM tokens: {},adv={}",
					andriodYouMengTokenList.size(), adv.getId());
			String pushKey = getPushKey(adv);
			int pushKeyRecordId = -1;
			st = System.currentTimeMillis();
			// if (adv.getTargetType() == Constants.PUSHTYPE_TARGET_AUTOPUSH) {
			// pushKey = adv.getShowType();
			// TimeLong.info("[AutoArriveAlert-Push] udidSize:{}, pushKey:{}, title:{}, costs {} ms",
			// udidList.size(), pushKey, adv.getTitle(),
			// (System.currentTimeMillis()-st));
			// } else {
			// pushKey = getPushKey(adv);
			// AdPushKey adPushKey = new AdPushKey(adv.getId(),
			// Integer.valueOf(rule.getRuleId()), udidList.size()
			// , iosTokenList.size(),
			// andriodGeTuiTokenList.size()+andriodYouMengTokenList.size(),
			// pushKey);
			// if (advService.saveAdPushKey(adPushKey) == 1) {
			// pushKeyRecordId = adPushKey.getId();
			// logger.info("PushRequestTask insert adpushKey adv={} ruleId:{} pushkey:{} success recordId:{}, costs {} ms",
			// adv.getId(), Integer.valueOf(rule.getRuleId()), pushKey,
			// pushKeyRecordId, (System.currentTimeMillis()-st));
			// } else {
			// logger.info("PushRequestTask insert adpushKey adv={} ruleId:{} pushkey:{} fail, costs {} ms",
			// adv.getId(), Integer.valueOf(rule.getRuleId()), pushKey,
			// (System.currentTimeMillis()-st));
			// }
			// }
			adsPushService.sendNotice(andriodGeTuiTokenList, adv, Platform.GT,
					endDate, pushKey, pushKeyRecordId, rule);
			adsPushService.sendNotice(iosTokenList, adv, Platform.IOS, endDate,
					pushKey, pushKeyRecordId, rule);
			adsPushService.sendNotice(andriodYouMengTokenList, adv, Platform.YM,
					endDate, pushKey, pushKeyRecordId, rule);
		} catch (Exception e) {
			e.printStackTrace();
//			TimeLong.error("PushRequestTask exception:", e);
			logger.error("pushERRO");
			logger.error(e.getMessage(), e);
		}
		TimeLong.info("PushRequestTask cost {} ms",
				(System.currentTimeMillis() - st));
	}

	private String getPushKey(AdContent adv) {
		String advId = (adv == null) ? "000" : "" + adv.getId();
		return advId;
	}

}
