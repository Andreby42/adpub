package com.bus.chelaile.flowNew;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.common.CacheUtil;
import com.bus.chelaile.flowNew.model.PayInfo;
import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.util.config.PropertiesUtils;

public class PayService {
	
	private static final String PAY_URL = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "pay.url",
			"http://dev.web.chelaile.net.cn/iccard/");

	protected static final Logger logger = LoggerFactory.getLogger(PayService.class);
	
	//获取乘车码信息
	public PayInfo getPayInfo(AdvParam param) {
		// 大爷OCS，判断城市
		// 大爷OCS，判断线路
		// 判断accountId
		// 坤朋OCS，判断是否领取
		logger.info("********* cityId={}, accountId={}, lineNO={}, lineId={}", param.getCityId(), param.getAccountId(), 
				param.getLineNo(), param.getLineId());
		if(! param.getCityId().equals("027")) {
			return null;
		}
		
		// ios无法确保lineId和lineNo同时获取到，所以需要根据lineId来获取lineNo
		if((StringUtils.isNoneBlank(param.getLineNo()) && param.getLineNo().equals("620")) || 
				(StringUtils.isNoneBlank(param.getLineId()) && param.getLineId().contains("010-620")) ||
				(StringUtils.isNoneBlank(param.getLineNo()) && param.getLineNo().equals("108")) || 
				(StringUtils.isNoneBlank(param.getLineId()) && param.getLineId().contains("010-108"))) {
			PayInfo payInfo = new PayInfo();
			payInfo.setName("乘车码");
			payInfo.setSlogan("乘车扫码，更加快捷");  // 跟随城市可配置
			payInfo.setUrl(PAY_URL);
			payInfo.setIcon("https://image3.chelaile.net.cn/4ca9504bc2ef4badb70ba43ba5d9f1ac");
			if(StringUtils.isBlank(param.getAccountId())) {		 // 未登录
				return payInfo;
			}
			String key = "cllAccountCityKey#" + param.getAccountId() + "#" + param.getCityId();
			logger.info("********** key={}", key);
			String isSupportPay = CacheUtil.getIsSupportAccountId(key);
			logger.info("********** key={}, isSupportPay={}", key, isSupportPay);
			if(isSupportPay == null || isSupportPay.equals("0")) { //已登录，未领取
				return payInfo;
			}
		}
//		if(StringUtils.isNoneBlank(param.getAccountId()) && param.getCityId().equals("027")
////				&& param.getLineId().equals("010-620-0")
//				) {
//			return payInfo;
//		}
		return null;
	}
}
