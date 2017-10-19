//package com.bus.chelaile.quchenshi;
//
//import java.util.List;
//
//
//
//
//import org.apache.http.NameValuePair;
//import org.apache.http.message.BasicNameValuePair;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.bus.chelaile.common.AdvCache;
//import com.bus.chelaile.common.CacheUtil;
//import com.bus.chelaile.common.TimeLong;
//import com.bus.chelaile.model.PropertiesName;
//import com.bus.chelaile.mvc.AdvParam;
//import com.bus.chelaile.quchenshi.model.QrCode;
//import com.bus.chelaile.quchenshi.model.QuchenshiDto;
//import com.bus.chelaile.thread.Queue;
//import com.bus.chelaile.thread.model.QueueObject;
//import com.bus.chelaile.util.CryptAES;
//import com.bus.chelaile.util.CypherHelper;
//import com.bus.chelaile.util.DateUtil;
//
//import com.bus.chelaile.util.HttpUtils;
//import com.bus.chelaile.util.JsonBinder;
//import com.bus.chelaile.util.New;
//import com.bus.chelaile.util.config.PropertiesUtils;
//
//public class QuchenshiHelp {
//
//	private static final Logger logger = LoggerFactory.getLogger(QuchenshiHelp.class);
//	private static final int QRCODELIMIT = Integer.parseInt(PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
//			"qrcodelimit", "2000"));
//	private static final String POSTURL = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "quchenshi.post.url", "http://watsons.weixinyiwindow.com/activity/qrcode4in1/index.html");
//	private static final String POSTKEY = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "quchenshiKey");
//	/**
//	 * 判断是否还有券
//	 * @param udid
//	 * @return	false	没券了,true	还有券
//	 */
//	public boolean isExistQr(String udid){
//		// 判领取数目是否已达上限
//		if (overGainedNum(udid)) {
//			return false;
//		}
//		return true;
//	}
//	
//	/*
//	 * 从屈臣氏接口获取数据
//	 */
//	public QrCode getInfoByApi(AdvParam advParam) {
//		String udid = advParam.getUdid();
//		
//		// 判领取数目是否已达上限
//		if (overGainedNum(udid)) {
//			QrCode qrcode = new QrCode();
//			qrcode.setStatus(1);	//返回status=1
//			logger.info("已经领完了,udid={}",advParam.getUdid());
//			return qrcode;
//		}
//		
//		
//		// 解析返回包
//		QrCode code = null;
//		String response = null;
//		try{
//			// 调用接口
//			response =  getResponseFromApi(advParam.getUdid());
//			code =  dealResponse(response,advParam.getUdid());
//		}catch(Exception e) {
//			e.printStackTrace();
//			logger.error(e.getMessage(),e);
//			logger.error("屈臣氏接口获取code失败！ udid={}, response={},errorInfo={}", udid, response,e.getMessage());
//			return null;
//		}
//		if(code == null) {
//			logger.error("屈臣氏接口返回code为空！ udid={}, response={}", udid, response);
//			return null;
//		}
//		
//		// 生成二维码到本地
////		String picName = getPicFileName(code);
////		String fileLocal = quchenshiDir + picName;
////		QrgenUtils.encoderQRCode(code, fileLocal, "jpg");
////		File file = new File(fileLocal);
////
////		// 上传二维码到oss
////		String qrcodeUrl = null;
////		try {
////			qrcodeUrl = OSSUtil.putPhoto(picName, file, "image/jpeg", folder);
////		} catch (FileNotFoundException e) {
////			e.printStackTrace();
////		}
////		if(qrcodeUrl == null) {
////			logger.error("上传二维码图片失败！, udid={}", udid);
////			return null;
////		}
//		
//		// 保存 用户领取的二维码  到 ocs
////		QrCode qrcode = new QrCode(code, qrcodeUrl);
//		
//		
//		if( code.getStatus() != 1 ){
//			// 记录 领取二维码的次数到 redis
//			setGained();
//			saveQrcode(udid, code);
//		}
//		
//		
//		return code;
//	}
//
//	
//	private String getResponseFromApi(String unique_id) throws Exception {
//		
//		List<NameValuePair> pairs = New.arrayList();
//		pairs.add(new BasicNameValuePair("mch", "chelaile"));
//		pairs.add(new BasicNameValuePair("unique_id", unique_id));
//		
//		String md5 = CypherHelper.genMD5("mch=chelaile|unique_id="+unique_id+"&&"+ POSTKEY);
//		pairs.add(new BasicNameValuePair("sign",md5 ));
//		return HttpUtils.post(POSTURL, pairs, "utf-8");
//
//	}
//
//
//	private QrCode dealResponse(String response,String udid) throws Exception {
//		if( response.indexOf(",coupon") != -1 ){
//			response = response.replace(",coupon", ",\"coupon");
//		}
//		QuchenshiDto dto =JsonBinder.fromJson(response, QuchenshiDto.class, JsonBinder.nonNull);
//		if( dto.getState_code() == null ){
//			throw new IllegalArgumentException("返回的code值为空");
//		}
//		if( !dto.getState_code().equals("1") && !dto.getState_code().equals("1001") && !dto.getState_code().equals("1002") ){
//			throw new IllegalArgumentException("返回的code值错误,值是:"+dto.getState_code());
//		}
//		//构建自己的返回包结构，以及 解码
//		QrCode code = new QrCode(dto.getCoupon(),dto.getQrcode());
//		//code.setCode(CryptAES.AES_Decrypt(POSTKEY, dto.getCoupon()));
//		//活动券码已发放完毕
//		if( dto.getState_code().equals("1001") || dto.getState_code().equals("1002")  ){
//			TimeLong.info("接口二维码领取完毕|用户重复领取,udid={},response={}", udid,response);
//			code.setStatus(1);
//		}
//		return code;
//	}
//
//
//	private String getPicFileName(String code) {
//		return DateUtil.getTodayStr("yyyy-MM-dd") + "_" + code + ".jpg";
//	}
//
//
//	private void saveQrcode(String udid, QrCode qrcode) {
//		if(udid == null || qrcode == null) {
//			logger.error("保存qrcode失败");
//			return;
//		}
//		AdvCache.saveQrcode(udid, qrcode);
//		
//		TimeLong.info("接口获取code成功：udid={}, qrcode={}", udid ,qrcode);
//	}
//
//	private boolean overGainedNum(String udid) {
//		
//		long time = System.currentTimeMillis();
//		String todayStr = DateUtil.getTodayStr("yyyy-MM-dd");
//		Object value = CacheUtil.getFromRedis(todayStr + "_gained");
//		if (value == null) {
//			return false;
//		}
//
//		time = System.currentTimeMillis() - time;
//		if (time > 40) {
//			TimeLong.info("getGainedNum cost time={}", time);
//		}
//
//		if (value instanceof String) {
//			int count = Integer.parseInt((String) value);
//			if (count >= QRCODELIMIT) {
//				logger.info("overGainedNum return true, count={}, qrcodelimit={},udid={}", count, QRCODELIMIT,udid);
//				return true;
//			}
//		}
//		return false;
//	}
//
//	/*
//	 * 用redis存储每天领取的二维码数目
//	 */
//	private void setGained() {
//		String todayStr = DateUtil.getTodayStr("yyyy-MM-dd");
//		QueueObject queueobj = new QueueObject();
//		queueobj.setRedisIncrKey(todayStr + "_gained");
//		Queue.set(queueobj);
//	}
//
//}
