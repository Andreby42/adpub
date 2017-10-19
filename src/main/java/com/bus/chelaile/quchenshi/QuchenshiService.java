//package com.bus.chelaile.quchenshi;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import com.bus.chelaile.common.AdvCache;
//import com.bus.chelaile.common.CacheUtil;
//import com.bus.chelaile.mvc.AdvParam;
//import com.bus.chelaile.push.PushService;
//import com.bus.chelaile.quchenshi.model.QrCode;
//import com.bus.chelaile.util.DateUtil;
//
//public class QuchenshiService {
//
//	private static final Logger logger = LoggerFactory.getLogger(QuchenshiService.class);
//
//	@Autowired
//	private QuchenshiHelp quchengshiHelp;
//	@Autowired
//	private PushService pushService;
//
//	/**
//	 * 从缓存中获取用户的二维码
//	 * 
//	 * @param advParam
//	 * @return 
//	 * null 用户未获取过
//	 * string 用户之前获取过的qrcodeurl
//	 */
//	public QrCode getQrCodeFromOCS(AdvParam advParam) {
//		String udid = advParam.getUdid();
//		QrCode qrcode = AdvCache.getQrcodeFromOCS(udid);
//		if(qrcode != null) {
//			if( qrcode.getCode() == null || qrcode.getCode().equals("") ){
//				logger.info("保存的code为空,udid={}, qrcode={}", udid, qrcode);
//				return null;
//			}
//			logger.info("用户已经领取过二维码,udid={}, qrcode={}", udid, qrcode);
//		}
//		return qrcode;
//	}
//	
//	
//	public String setQrCodeToOcs(String udid,String code){
//		QrCode qr = new QrCode(code,"");
//		AdvCache.saveQrcode(udid, qr);
//		return code;
//	}
//	
//	
//	public String isExistQr(String udid){
//		if( quchengshiHelp.isExistQr(udid) ){
//			return "1";
//		}
//		return "0";
//	}
//
//	/**
//	 * 获取屈臣氏优惠二维码
//	 * 
//	 * @param advParam
//	 * @return
//	 */
//	public QrCode getQrCode(AdvParam advParam) {
//		String udid = advParam.getUdid();
//
//		//查看当日领取二维码的人数
//		if (udid.equals("12e20279-d650-47c1-8ace-d8a8f4672deb")) {
//			String todayStr = DateUtil.getTodayStr("yyyy-MM-dd");
//			Object value = CacheUtil.getFromRedis(todayStr + "_gained");
//			logger.info("today={}, gainedNum={}", todayStr, value);
//		}
//		
//		//判断是否领取过二维码
//		QrCode qrCode = getQrCodeFromOCS(advParam);
//		if(qrCode != null) {
//			//logger.error("领取过二维码的用户，再次抽奖！,udid={},qrcode={}",udid, qrCode);
//			return qrCode;
//		}
//
//		// 从接口获取数据
//		return quchengshiHelp.getInfoByApi(advParam);
//		
//
//	}
//
//	/*
//	 * 校验用户是否有效
//	 */
//	public boolean checkRealUser(String udid) {
//		if(udid == null) {	 //udid为空发，非法
//			return false;
//		}
//		if (! AdvCache.isRealUsers(udid)) { 
//			//udid没有在有效缓存中找到
//			logger.error("udid有效缓存中没有找到，udid={}", udid);
//			
//			//udid拿不到token
//			List<String> udidList = new ArrayList<String>();
//			udidList.add(udid);
//			Map<String, String> tokenMap = pushService.getTokenByUdidsFromOcs(udidList);
//			String tokenStr = tokenMap.get(udid);
//			if (StringUtils.isBlank(tokenStr)) {
//				logger.info("没有找到token，非法的udid={}", udid);
//				return false;
//			}
//		}
//		
//		return true;
//	}
//}
