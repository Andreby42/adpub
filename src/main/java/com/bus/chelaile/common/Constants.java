package com.bus.chelaile.common;

import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.util.config.PropertiesUtils;

public class Constants {
    public static final int STATUS_SUCC = 0;
    public static final int STATUS_FAIL = -1;
    
    public static final int INT_UNDEFINED = -1;
    
    public static final String STATUS_REQUEST_SUCCESS = "00";
    public static final String STATUS_INTERNAL_ERROR = "02";
    public static final String STATUS_FUNCTION_NOT_ENABLED = "03";
    public static final String STATUS_NO_DATA = "00";
    public static final String STATUS_PARAM_ERROR = "05";
    public static final String STATUS_NO_ARTICLES = "11";	// 没有更多文章了！
    
    public static final String STATUS_ILLEGAL_OPERATION = "06";
    
    public static final int LONGEST_CACHE_TIME = 30 * 24 * 60 * 60 - 1; 
    public static final int ONE_DAY_TIME = 1 * 24 * 60 * 60; //一天，单位 S
    public static final int SEVEN_DAY_TIME = 7 * 24 * 60 * 60; //七天，单位 S
    
    public static final int ONE_DAY_NEW_USER_PERIOD = 1 * 24 * 60 * 60 * 1000;  //一天，单位毫秒，默认新用户的时期。
    public static final int DEFAULT_NEW_USER_PERIOD = 7 * 24 * 60 * 60 * 1000;  //七天，单位毫秒，默认新用户的时期。
    
    public static final boolean ISTEST = Boolean.parseBoolean(PropertiesUtils.getValue(
			PropertiesName.PUBLIC.getValue(), "isTest", "false"));
    public static final boolean IS_FLOW = Boolean.parseBoolean(PropertiesUtils.getValue(
			PropertiesName.PUBLIC.getValue(), "isFlow", "false"));
    public static final boolean IS_CACHE_KOUBEI = Boolean.parseBoolean(PropertiesUtils.getValue(
			PropertiesName.PUBLIC.getValue(), "isCacheKoubei", "false"));
    
    // position属性未设定。
    public static final int NULL_POSITION = -99;
    
    // redirect参数名称
    public static final String PARAM_AD_ID = "advId";
    public static final String PARAM_AD_TYPE = "adtype";
    public static final String PARAM_STATION_ORDER = "storder";
    public static final String PARAM_LINE_ORDER = "lorder";
    public static final String PARAM_STATION_NAME = "stname";
    public static final String PARAM_STATION_NAME_H5 = "stn_name";
    public static final String PARAM_DISTANCE = "distance";
    
    public static final String PARAM_DEVICE = "deviceType";
    public static final String PARAM_IP = "ip";
    public static final String PARAM_LNG = "lng";
    public static final String PARAM_LAT = "lat";
    public static final String PARAM_NW = "nw";
    public static final String PARAM_IMEI = "imei";
    public static final String PARAM_IDFA = "idfa";
    public static final String PARAM_UA = "userAgent";
    public static final String PARAM_S = "s";
    public static final String PARAM_ANDROID = "AndroidID";
    public static final String PARAM_MAC = "mac";
    public static final String PARAM_ACCOUNTID = "accountId";
    
    public static final String PARAM_STATION_LNG = "stn_lng";
    public static final String PARAM_STATION_LAT = "stn_lat";

    // 代表是积分商城推送的广告类型
    public static final int PUSHTYPE_TARGET_DUIBA = 1;
    // 代表是ugc推送的广告类型
    public static final int PUSHTYPE_TARGET_UGC = 2;
    // 代表是自动推送的广告类型
    public static final int PUSHTYPE_TARGET_AUTOPUSH = 4;
    // 代表话题推送
    public static final int PUSHTYPE_TARGET_HUATI = 3;
    // 代表是打开单车的广告类型
    public static final int DOUBLE_BICYCLE_ADV = 5;
    // 代表发现-信息流推送
    public static final int PUSHTYPE_FLOW = 6;


    //IOS推送内容的最大长度 220
    public static final int PUSH_IOS_BODYLENGTH_MAX = 292;  // online:292
    //区分IOS，ANDROID BODYLENGTH 的默认值
    public static final int PUSH_BODYLENGTH_DEFAULT = -999;
    
    public static final String IOSNAME = "ios";
    
    public static final String ANDROIDNAME = "android";
    
    public static final String EMPTY_STR = "";
    
    
    // kafka过滤日志，关键字和一些命名
    public static final int ROW_SKIP = 0;
	public static final int ROW_SHOW = 1;
	public static final int ROW_CLICK = 2;
	public static final int ROW_ARTICLE_MAIDIAN_CLICK = 3;
	public static final int ROW_TOUTIAO_CLICK = 4;
//	public static final int ROW_ADV_EXHIBIT = 5;	// 广告展示
	public static final int ROW_ADV_CLICK = 6;
	public static final int ROW_APP_INFO = 7;
	public static final int ROW_LINEDETAIL = 8;
	public static final int ROW_OPEN_ADV_EXHIBIT = 9; // 开屏广告展示

	public static final String UC_ACTION_NAME = "adpub/adv!getUCArticles.action";
	public static final String AD_DOMAIN_NAME = "ad.chelaile.net.cn ";	//第三方广告跳转链接，redirect只用于我们自己内部的活动跳转
	public static final String REDIRECT_DOMAIN_NAME = "redirect.chelaile.net.cn"; //redirect
//	public static final String INFORMATION_FLOW_KEYWORD = "chelaile-iflow";
	public static final String TOUTIAO_CLICK_KEYWORD = "toutiao"; // 头条点击url关键字
	public static final String MAIDIAN_LOG = "logs.chelaile";
	public static final String TEST_MAIDIAN_LOG = "dev.logs.chelaile.net.cn";
	public static final String ARTICLE_CLICK = "ARTICLE_CLICK";
	public static final String USER_ID_REG_EX = "&udid=(.*?)&";
	public static final String ARTICLE_ID_REG_EX = "&aid=(.*?)&";
	public static final String ADV_EXHIBIT = "ADV_EXHIBIT";
	public static final String APP_INFO_LOG = "APP_INFO";
	public static final String LINEDETAIL = "/bus/line!lineDetail.action";
	public static final String FOR_DEVELOP_EXHIBIT = "FOR_DEVELOP_EXHIBIT";		//开屏广告展示失败的埋点。这个数据可能会影响到点击数统计，so需要滤掉
	public static final String OPEN_ADV_KEYWORD = "adv_type:1"; // 开屏广告埋点，类型关键字
 	
    
    public static final String STATSACT_REFRESH = "refresh";
    public static final String STATSACT_ENTER = "enter";
    public static final String STATSACT_AUTO_REFRESH = "auto_refresh";
    public static final String STATSACT_SWITCH_STN = "switch_stn";
    public static final String STATSACT_REVERSE = "reverse";
    
    public static final String CSHOW_LINEDETAIL = "linedetail";		// cshow参数值
    
    // redis 分布式锁
    public static final int EXPIREMSECS = 1000;		// 1 S
    public static String LOCKKEY = "lockKey";
    
    // 版本控制号
    public static final int PLATFORM_LOG_ANDROID_0118 = 96; // 3.45.0
	public static final int PLATFORM_LOG_IOS_0117 = 10480; // 5.43.0
	
	public static final int PLATFORM_LOG_ANDROID_0208 = 97; // 3.46.0
	public static final int PLATFORM_LOG_IOS_0208 = 10490; // 5.44.0
	
	public static final int PLATFORM_LOG_ANDROID_0326 = 100; // 3.48.0
	public static final int PLATFORM_LOG_IOS_0326 = 10510; // 5.46.0
	
	public static final int PLATFORM_LOG_ANDROID_0420 = 102; //3.50.0
	public static final int PLATFORM_LOG_IOS_0420 = 10530; // 5.48.0
}
