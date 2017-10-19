package com.bus.chelaile.push;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.common.AdvCache;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.common.TimeLong;
import com.bus.chelaile.dao.AppAdvContentMapper;
import com.bus.chelaile.dao.AppAdvRuleMapper;
import com.bus.chelaile.model.Platform;
import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.model.ads.AdContent;
import com.bus.chelaile.model.ads.AdInnerContent;
import com.bus.chelaile.model.ads.AdPushInnerContent;
import com.bus.chelaile.model.rule.AdRule;
import com.bus.chelaile.model.rule.Rule;
import com.bus.chelaile.model.rule.RuleEngine;
import com.bus.chelaile.push.task.PushRequestTask;
import com.bus.chelaile.util.AdvUtil;
import com.bus.chelaile.util.FileUtil;
import com.bus.chelaile.util.New;
import com.bus.chelaile.util.OnsUtil;
import com.bus.chelaile.util.config.PropertiesUtils;

/**
 * 新推送处理类,推送广告系统的内容
 * 
 * @author zzz
 * 
 */
public class AdsPushService extends AbstractPushManager {

	protected static final Logger logger = LoggerFactory.getLogger(AdsPushService.class);

	@Autowired
	private AppAdvContentMapper contentDao;

	@Autowired
	private AppAdvRuleMapper ruleDao;

	private static final int MAX_TOKENS_PER_TIME = 100;

	private static final int MAX_TOKENS_PER_TIME_YOUMENG = Integer.parseInt(PropertiesUtils.getValue(
			PropertiesName.PUBLIC.getValue(), "push.youmeng.percount", "200000"));

	private static final String PUSH_WRITE_TOKEN_FILE_PATH = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			"push.adv.udid.file.prefix", "/data/remind/token/");
	private static final String PUSH_WRITE_TOKEN_LOCAL_FILE_PATH = PropertiesUtils.getValue(
			PropertiesName.PUBLIC.getValue(), "push.adv.udid.local.file.prefix", "/data/outman/udid/");

	private static ExecutorService threadPool = Executors.newFixedThreadPool(Integer.parseInt(PropertiesUtils.getValue(
			PropertiesName.PUBLIC.getValue(), "udidToken.toOcs.threadcount")));

	/**
	 * 推送广告
	 * 
	 * type=all的时候推送全部
	 * 否则 推送内部人员
	 * 
	 * @param advId
	 */
	public boolean pushAds(int advId, String ruleId, String ruleIds, String type, boolean isPushAndroidAll) {
		AdContent ad = contentDao.query4Id(advId);
		List<Integer> ridList = parseRuleIds(ruleId, ruleIds);
		List<AdRule> ruleList = ruleDao.list4AdvIdByTime(advId, new Date());
		List<Rule> sendList = New.arrayList();
		
		boolean isInside = false;
		if(type != null && type.equals("inside")) {	//如果 type 等于  inside，那么只给内部用户推送
			isInside = true;
		}
		
		for (int rId : ridList) {
			AdRule adRule = null;
			for (AdRule ar : ruleList) {
				if (ar.getRuleId() == rId) {
					adRule = ar;
					break;
				}
			}
			logger.info("Queried Rule: {}", adRule);
			Rule rule = RuleEngine.parseRule(adRule);
			if (rule == null) {
				logger.error("Cann't parse the AdRule:{}", adRule);
				return false;
			}
			if (rule.getUserIds() != null && rule.getUserIds().size() == 0) {
				continue;
			}
			sendList.add(rule);
		}
		preparePushNotice(ad, sendList, isInside, isPushAndroidAll);
		return true;
	}

	private void preparePushNotice(AdContent adv, List<Rule> ruleList, boolean isInside, boolean isPushAndroidAll) {
		// 首先暂时不考虑各条RULE之中满足的条件的udid有可能重复的情况。
		String adKey = genAdCacheKey(adv);
		List<String> requestIdList = new ArrayList<>();
		for (Rule rule : ruleList) {
			if (rule.hasUserIds()) {
				// 根据UDID直接进行投放， udid可以放在udidFile之中。
				sendPushNoticeByUDID(adv, rule.getUserIds(), rule.getEndDate(), rule, isInside);
				logger.info("[SEND_PUSH_NOTICE] adv={}, ruleId={}, udidListCount={},adKey={}, isPushAndroidAll={}", adv, rule.getRuleId(),
						rule.getUserIds().size(), adKey, isPushAndroidAll);
			}
		}

		logger.info("Requested UserList for AdContent: adKey={}, requestIds={},adv={}", adKey, requestIdList,
				adv.getId());
	}

	private void sendPushNoticeByUDID(AdContent adv, List<String> udidList, Date endDate, Rule rule, boolean isInside) {

		TimeLong.info("需要推送的, adv={},udidList.size={}", adv, udidList.size());

		if (adv == null || udidList == null) {
			TimeLong.info("无法推送消息: 输入的UDID列表为空, adv={}", adv.getId());
			return;
		}
		TimeLong.info("根据输入的UDID列表进行推送, adv={}, rule={}", adv.getId(), rule.getRuleId());
		long st = System.currentTimeMillis();
		// 去掉重复的advId，ruleId请求
//		if (udidList.size() > 30 && adv.getTargetType() != Constants.PUSHTYPE_TARGET_AUTOPUSH) {
//			if (AdvCache.isExist(adv.getId(), Integer.valueOf(rule.getRuleId()))) {
//				TimeLong.info("adv={}, rule={} exist", adv.getId(), rule.getRuleId());
//				return;
//			}
//			AdvCache.setPushRequestToCache(adv.getId(), Integer.valueOf(rule.getRuleId()));
//		}

		int maxSize = 1000;

		if (udidList.size() < maxSize) {
			threadPool.execute(new PushRequestTask(adv, udidList, rule, this, endDate, isInside));

			TimeLong.info("sendPushNoticeByUDID type: {} cost {} ms,size={}", adv.getTargetType(),
					(System.currentTimeMillis() - st), udidList.size());
			return;
		}

		int toIndex = maxSize;

		int fromIndex = 0;

		while (true) {

			toIndex += maxSize;

			if (toIndex >= udidList.size()) {
				toIndex = udidList.size();
			}

			List<String> list = New.arrayList();

			for (int i = fromIndex; i < toIndex; i++) {
				list.add(udidList.get(i));
			}
			try {
				threadPool.execute(new PushRequestTask(adv, list, rule, this, endDate, isInside));
			} catch (Exception e) {
				TimeLong.info("错误的推送信息:" + e.getMessage());
			}

			TimeLong.info("sendPushNoticeByUDID fromIndex={},toIndex={}", list.get(0), list.get(list.size() - 1));

			fromIndex = toIndex;

			if (toIndex >= udidList.size()) {
				return;
			}
		}

	}

	private String genAdCacheKey(AdContent ad) {
		if (ad == null) {
			return null;
		}

		StringBuffer sb = new StringBuffer();

		sb.append(ad.getId()).append('#').append(ad.getShowType()).append('#').append(ad.getStatus()).append('#')
				.append(toTimeStr(ad.getCreateTime())).append('#').append(toTimeStr(ad.getModifyTime()));

		return sb.toString();
	}

	private String toTimeStr(Date date) {
		if (date == null) {
			return "null";
		}
		return Long.toString(date.getTime());
	}

	private List<Integer> parseRuleIds(String ruleId, String ruleIds) {
		Set<Integer> idSet = new HashSet<Integer>();

		idSet.addAll(parseRule(ruleId));
		idSet.addAll(parseRule(ruleIds));

		return new ArrayList<Integer>(idSet);
	}

	private static List<Integer> parseRule(String ruleStr) {
		List<Integer> idList = new ArrayList<Integer>();

		if (ruleStr != null && !ruleStr.isEmpty()) {
			ruleStr = ruleStr.trim();
			if (ruleStr.contains(",")) {
				String[] parts = ruleStr.split(",");
				for (int i = 0; i < parts.length; i++) {
					try {
						int id = Integer.parseInt(StringUtils.trim(parts[i]));
						idList.add(id);
					} catch (NumberFormatException nfe) {
						logger.error("NumberFormatException 规则的格式错误, ruleId={}, errMsg={}", ruleStr, nfe.getMessage());
					}
				}
			} else {
				try {
					int id = Integer.parseInt(ruleStr);
					idList.add(id);
				} catch (NumberFormatException nfe) {
					logger.error("NumberFormatException 规则的格式错误, ruleId={}, errMsg={}", ruleStr, nfe.getMessage());
				}
			}
		}

		return idList;
	}


	public void sendNotice(List<String> tokenList, AdContent adv, Platform platform, Date endDate, String pushKey,
			int pushKeyRecordId, Rule rule) {
		long st = System.currentTimeMillis();
		long st1 = st;
		if (tokenList == null || tokenList.isEmpty()) {
			return;
		}
		JSONObject sendMsg = new JSONObject();
		// 当前时间+3
		String expireTime = buildExpireTime(endDate, platform);
//		boolean isIos = false;
		if (platform == Platform.IOS) {
//			isIos = true;
		}
		sendMsg.put("expiredTime", expireTime);
		sendMsg.put("drives", platform.getValue());
		int tokenSize = tokenList.size();
		int perTime = MAX_TOKENS_PER_TIME;
		String waild = "%UDID%";
		if (platform == Platform.YM) {
			perTime = MAX_TOKENS_PER_TIME_YOUMENG;
		}
		if (StringUtils.isNotBlank(adv.getLink()) && adv.getLink().contains(waild)) {
			perTime = 1;
		}

		// 如果广告类型不是自动推送并且推送平台是友盟并且没有link设置,走友盟批量推送
		if (adv.getTargetType() != Constants.PUSHTYPE_TARGET_AUTOPUSH && platform == Platform.YM && perTime != 1) {
			sendYMNotice(tokenList, adv, platform, pushKey, rule, pushKeyRecordId, endDate);
		} else {
			int pushSuccessCount = 0;
			logger.debug("sendNotice tokenSize:" + tokenSize + " perTime: " + perTime + " platform " + platform);
			int passMaxLengthSize = 0;
			for (int idx = 0; idx < tokenSize; idx += perTime) {
				st = System.currentTimeMillis();
				String keyStr = tokenList.get(idx);
				String udid = StringUtils.substringAfter(keyStr, "#");
				if (platform == Platform.YM) {
					sendMsg.put("serialNumber", PushSerialNumberCreateUtil.getSerialNumber());
				}
				List<Object> bodyObjects = buildNoticeMsgBody(adv, platform, pushKey, udid);
				String msgBody = (String) bodyObjects.get(0);
				int bodyLength = (int) bodyObjects.get(1);
				sendMsg.put("msgBody", msgBody);
				List<Object> objects = AdvUtil.toJson(tokenList, idx, idx + perTime, bodyLength);
				JSONArray clientList = (JSONArray) objects.get(0);
				int passMaxClient = (int) objects.get(1);
				sendMsg.put("clients", clientList);
				passMaxLengthSize += passMaxClient;
				String msg = sendMsg.toJSONString();
				logger.info("Generated Msg: msg={}, passMaxLengthSize={} costs {} ms", msg, bodyLength,
						(System.currentTimeMillis() - st));
				st = System.currentTimeMillis();
				
				String res = OnsUtil.send(PUSH_KEY, msg);
				
				if (res.equalsIgnoreCase("-1")) {
					logger.info("[ONS_SEND_FAIL] 发送消息到ONS队列之中失败: adv={}, ruleId={}, res={}, costs {} ms", adv.getId(),
							rule.getRuleId(), res, (System.currentTimeMillis() - st));
				} else {
					logger.info("[ONS_SEND_SUCCESS] 发送消息到ONS队列之中成功: adv={}, ruleId={}, msgId={}, costs {} ms",
							adv.getId(), rule.getRuleId(), res, (System.currentTimeMillis() - st));
					saveTokenUdidToOcs(createUdidTokenList(idx, perTime, tokenList), res);
					logger.debug("sendNotice succe: " + pushSuccessCount);
					int surplusCount = tokenSize - idx;
					logger.debug("sendNotice surplusCount: " + surplusCount);
					if (surplusCount < perTime) {
						pushSuccessCount += surplusCount;
						logger.debug("sendNotice <: " + pushSuccessCount);
					} else {
						pushSuccessCount += perTime;
						logger.debug("sendNotice >=: " + pushSuccessCount);
					}
					logger.debug("sendNotice succecount: " + pushSuccessCount);
				}
			}
			// // 更新数据库数据
			// if (pushKeyRecordId > 0) {
			// pushSuccessCount = pushSuccessCount - passMaxLengthSize;
			// if (advService.updateAdPushKey(pushKeyRecordId, pushSuccessCount,
			// isIos) == 1) {
			// logger.info("sendNotice advId:{} pushKeyId: {}, pushSuccCount:{} isIos: {} success",
			// adv.getId(), pushKeyRecordId, pushSuccessCount, isIos);
			// } else {
			// logger.info("sendNotice advId:{} pushKeyId: {}, pushSuccCount:{} isIos: {} fail",
			// adv.getId(), pushKeyRecordId, pushSuccessCount, isIos);
			// }
			// }
			logger.info("完成发送广告： adv={}, rule={}, platform={}, 发送的token数={}, 超过220长度数={}, costs {} ms", adv,
					rule.getRuleId(), platform, tokenList.size(), passMaxLengthSize, (System.currentTimeMillis() - st1));
		}
	}

	private List<String> createUdidTokenList(int startIdx, int perTime, List<String> tokenList) {
		List<String> udidTokenList = new ArrayList<>();
		int tokenSize = tokenList.size() - startIdx > perTime ? perTime : tokenList.size() - startIdx;
		for (int idx = startIdx; idx < (tokenSize + startIdx); idx++) {
			String keyStr = tokenList.get(idx);
			udidTokenList.add(keyStr);
		}
		return udidTokenList;
	}

	/**
	 * 针对友盟抽取的推送通知 大数量时将udid写入文件并上传到指定服务器,目前每个文件的udid上限为20万
	 * 
	 * @param tokenList
	 *            token列表
	 * @param adv
	 *            广告
	 * @param platform
	 *            平台
	 * @param pushKey
	 *            推送key
	 * @param rule
	 *            规则
	 * @param pushKeyRecordId
	 *            推送记录id
	 * @param endDate
	 *            结束时间
	 */
	private synchronized void sendYMNotice(List<String> tokenList, AdContent adv, Platform platform, String pushKey, Rule rule,
			int pushKeyRecordId, Date endDate) {

		long st = System.currentTimeMillis();

		List<String> filePathList = new ArrayList<>();
		int fileCountNumber = 1;
//		int pushSuccessCount = 0;

		int tokenSize = tokenList.size();
		int fileCount = tokenSize % MAX_TOKENS_PER_TIME_YOUMENG == 0 ? tokenSize / MAX_TOKENS_PER_TIME_YOUMENG
				: tokenSize / MAX_TOKENS_PER_TIME_YOUMENG + 1;

		for (int i = 0; i < fileCount; i++) {

			List<String> udidList = filterUdidList(tokenList, i * MAX_TOKENS_PER_TIME_YOUMENG, (i + 1)
					* MAX_TOKENS_PER_TIME_YOUMENG);
			String finalClientStr = buildFinalContent(udidList);

			String fileName = writeAndUploadFile(adv, rule, fileCountNumber, finalClientStr);
			if (StringUtils.isNotEmpty(fileName)) {
				filePathList.add(fileName);
//				pushSuccessCount += udidList.size();
			}

			fileCountNumber++;
		}
		logger.info("write udid to file success, file count {}", fileCountNumber);

		if (CollectionUtils.isNotEmpty(filePathList)) {
			JSONObject sendMsg = new JSONObject();

			List<Object> bodyObjects = buildNoticeMsgBody(adv, platform, pushKey, "");
			String msgBody = (String) bodyObjects.get(0);

			sendMsg.put("serialNumber", PushSerialNumberCreateUtil.getSerialNumber());
			sendMsg.put("msgBody", msgBody);
			sendMsg.put("filePath", filePathList);

			String expireTime = buildExpireTime(endDate, platform);
			sendMsg.put("expiredTime", expireTime);
			sendMsg.put("drives", platform.getValue());
			String msg = sendMsg.toJSONString();

			logger.info("push msg is {}", msg);

			String res = OnsUtil.send(PUSH_KEY, msg);
			if (res.equalsIgnoreCase("-1")) {
				logger.error("[ONS_SEND_FAIL] 发送YM消息到ONS队列之中失败: adv={}, ruleId={}, res={}, costs {} ms", adv.getId(),
						rule.getRuleId(), res, (System.currentTimeMillis() - st));
			} else {
				logger.info("[ONS_SEND_SUCCESS] 发送YM消息到ONS队列之中成功: adv={}, ruleId={}, tokenSize={}, msgId={}, costs {} ms",
						adv.getId(), rule.getRuleId(), tokenSize, res, (System.currentTimeMillis() - st));

				saveTokenUdidToOcs(tokenList, res);
			}

			// // 更新数据库数据
			// if (pushKeyRecordId > 0) {
			// if (advService.updateAdPushKey(pushKeyRecordId,
			// pushSuccessCount, false) == 1) {
			// logger.info(
			// "sendNotice advId:{} pushKeyId: {}, pushSuccessCount:{} isIos: {} success",
			// adv.getId(), pushKeyRecordId, pushSuccessCount,
			// false);
			// } else {
			// logger.info(
			// "sendNotice advId:{} pushKeyId: {}, pushSuccessCount:{} isIos: {} fail",
			// adv.getId(), pushKeyRecordId, pushSuccessCount,
			// false);
			// }
			// }
		}
	}

	/**
	 * 根据约定规则(使用回车符进行分隔)构建内容
	 * 
	 * @param udidList
	 *            待处理列表
	 * @return 处理后列表
	 */
	private String buildFinalContent(List<String> udidList) {

		String separator = System.getProperty("line.separator", "\n");
		StringBuilder sb = new StringBuilder(1000);

		for (String udid : udidList) {
			sb.append(udid).append(separator);
		}
		return sb.toString();
	}

	/**
	 * 从指定列表中过滤出指定区间的udid集合,包含开始位置不包含结束位置
	 * 
	 * @param tokenList
	 *            udid#token集合
	 * @param start
	 *            开始位置
	 * @param end
	 *            结束位置
	 * @return udid列表
	 */
	private List<String> filterUdidList(List<String> tokenList, int start, int end) {

		List<String> udidList = new ArrayList<>();
		int size = tokenList.size();
		end = end >= size ? size : end;

		for (int i = start; i < end; i++) {
			String keyStr = tokenList.get(i);
			String clientStr = StringUtils.substringBefore(keyStr, "#");
			udidList.add(clientStr);
		}
		return udidList;
	}

	private List<Object> buildNoticeMsgBody(AdContent adv, Platform platform, String pushKey, String udid) {
		List<Object> resultList = new ArrayList<>();
		if (adv == null || adv.getContent() == null) {
			logger.error("AdContent is NULL.");
			resultList.add("");
			resultList.add(Constants.PUSH_BODYLENGTH_DEFAULT);
			return resultList;
		}
		String content = "";
		String title = "";
		String type = "2";
		boolean isRepeatParam = true;
		if(adv.getTargetType() == Constants.PUSHTYPE_FLOW) {
			type = "6";
		} else if (adv.getTargetType() == Constants.PUSHTYPE_TARGET_DUIBA) {
			type = "3";
		} else if (adv.getTargetType() == Constants.PUSHTYPE_TARGET_UGC) {
			type = "4";
		} else if (adv.getTargetType() == Constants.PUSHTYPE_TARGET_HUATI) {
			type = "5";
		}
		if (adv.getTargetType() == Constants.PUSHTYPE_TARGET_AUTOPUSH) {
			content = adv.getContent();
			title = adv.getTitle();
			isRepeatParam = false;
		} else {
			AdInnerContent innerContent = adv.getInnerContent();
			AdPushInnerContent pushInner = null;
			if (!(innerContent instanceof AdPushInnerContent)) {
				logger.error("innerContent type is NOT AdPushInnerContent: {}", innerContent);
				resultList.add("");
				resultList.add(Constants.PUSH_BODYLENGTH_DEFAULT);
				return resultList;
			}
			pushInner = (AdPushInnerContent) innerContent;
			content = pushInner.getSubhead();
			title = pushInner.getHead();
		}
		String tranferdUrl = AdvUtil.buildRedirectLink(adv.getLink(), PUSH_NOTICE_PARAM_MAP, udid, true, isRepeatParam,
				adv.getLink_extra());
		return buildMsgJSonString(type, title, content, pushKey, tranferdUrl, platform, adv.getOpenType());
	}


	/**
	 * 写文件并上传到服务器
	 * 
	 * @param adv
	 *            广告
	 * @param rule
	 *            规则
	 * @param fileCountNumber
	 *            文件记录数
	 * @param finalClientStr
	 *            文件内容
	 * @return 操作成功的文件名
	 */
	private String writeAndUploadFile(AdContent adv, Rule rule, int fileCountNumber, String finalClientStr) {
		String fileName = buildFileName(adv.getId(), Integer.parseInt(rule.getRuleId()), fileCountNumber);
		boolean writeFileSuccess = FileUtil.writeContentToFile(PUSH_WRITE_TOKEN_LOCAL_FILE_PATH, fileName,
				finalClientStr);
		if (writeFileSuccess) {
			boolean uploadFileSuccess = FileUtil.uploadFileToRemote(PUSH_WRITE_TOKEN_LOCAL_FILE_PATH, fileName,
					PUSH_WRITE_TOKEN_FILE_PATH);
			if (uploadFileSuccess) {
				return fileName;
			} else {
				logger.error("upload udid file to remote fail, advId {}, ruleId {}, fileCountNumber {}", adv.getId(),
						rule.getRuleId(), fileCountNumber);
			}
		} else {
			logger.error("write udid to file fail, advId {}, ruleId {}, fileCountNumber {}", adv.getId(),
					rule.getRuleId(), fileCountNumber);
		}
		return "";
	}

	/**
	 * 构建存储token的文件名
	 * 
	 * @param advId
	 *            广告id
	 * @param ruleId
	 *            规则id
	 * @param count
	 *            计数
	 * @return 完整文件名
	 */
	private String buildFileName(int advId, int ruleId, int count) {
		return advId + "-" + ruleId + "-" + count + ".txt";
	}
	
	
    public String getUrlFromShortUrl(String shortUrl) {
        String urlFromCache = AdvCache.getUrlFromCacheByShortUrl(shortUrl);
        if (StringUtils.isNotBlank(urlFromCache)) {
            return urlFromCache;
        }
        return null;
    }
}
