package com.bus.chelaile.third.kedaxunfei.model;

/**
 * 设备系统信息
 * 
 * @author 41945
 *
 */
public class RequestModel {
	/**
	 * y 设备类型
	 * 
	 * -1-未知 0 - phone 1 - pad 2 - pc 3 - tv 4 - wap
	 * 
	 */
	private String devicetype;
	/**
	 * y 客户端操作系统的类型(具体传系统值,如样例:Android)
	 * 
	 * Android iOS WP Others(注意大 小写)
	 * 
	 */
	private String os;
	/**
	 * y 操作系统版本号
	 */
	private String osv;
	/**
	 * iOS 系统 openudid, openudid sha1, openudid md5 必填其一
	 */
	private String openudid;
	/**
	 * iOS 系统 openudid, openudid sha1, openudid md5 必填 其一
	 */
	private String openudidsha1;
	/**
	 * iOS 系统 openudid, openudid sha1, openudid md5 三者 必填其一
	 * 
	 */
	private String openudidmd5;
	/**
	 * Android 系 统 adid,adids ha1,adid md5 三者 必填其一
	 */
	private String adid;
	/**
	 * Android 系 统 adid,adids ha1,adid md5 三者 必填其一
	 */
	private String adidsha1;
	/**
	 * Android 系 统 adid,adids ha1,adid md5 三者 必填其一
	 */
	private String adidmd5;
	/**
	 * Android 系 统 imei,imei sha1,imei md5 三者必填其一
	 */
	private String imei;
	/**
	 * Android 系 统 imei,imei sha1,imei md5 三者 必填其一
	 * 
	 */
	private String imeisha1;
	/**
	 * Android 系 统 imei,imei sha1,imei md5 三者 必填其一
	 * 
	 */
	private String imeimd5;
	/**
	 * Y for iOS
	 */
	private String idfa;
	/**
	 * Y for iOS(>=iO S6)
	 * 
	 */
	private String idfv;
	/**
	 * Android 系统、 iOS6以下 包括iOS6 系 统 mac,macs ha1,macm d5 三者必 填其一
	 * 
	 */
	private String mac;
	/**
	 * Android 系统、 iOS6以下 包括iOS6 系 统 mac,macs ha1,macm d5 三者必 填其一
	 */
	private String macsha1;

	private String macmd5;
	/**
	 * Recommended AAID(AdvertisingId) Advertising Id ?
	 * 
	 */
	private String aaid;
	/**
	 * Y for WP Windows Phone 用户终端的 DUID
	 */
	private String duid;
	/**
	 * Y 屏幕密度
	 */
	private String density;
	/**
	 * Y 网络运营商，取值： “46000” (即中国 移s 动 ) ，“ 46001 ” （即中国 联 通 ）， “46003”（即中国 电信）
	 * 
	 * （若获取 不到默认 “ 46000 ”）
	 * 
	 * 
	 */
	private String operator = "46000";
	/**
	 * Y 联网类型(0—未知， 1—Ethernet ， 2—wifi，3—蜂窝网 络，未知代，4—， 2G，5—蜂窝网络， 3G，6—蜂窝网络， 4G)
	 * 
	 */
	private String net = "0";
	/**
	 * y 客户端 ip
	 */
	private String ip;
	/**
	 * y User-Agent(字符串, 需 escape 转义)
	 * 
	 * UA（必须是默认 浏览器的 ua）
	 * 
	 * 
	 */
	private String ua;
	/**
	 * y 发送请求时的本地 UNIX 时间戳(10 进 制)TS 看一下是秒还是ms
	 * 
	 */
	private String ts;
	/**
	 * y 广告位的宽度，以像 素为单位。(指密度 无关像素，即 DIP 或 CSS pixel)
	 * 
	 */
	private String adw;
	/**
	 * y 广告位的高度，以像 素为单位。( 指密度 无关像素，即 DIP 或 CSS pixel)
	 */
	private String adh;
	/**
	 * y 设备屏幕的宽度，以 像素为单位。(指密 度无关像素，即 DIP 或 CSS pixel)
	 * 
	 */
	private String dvw;
	/**
	 * y 设备屏幕的高度，以 像素为单位。(指密 度无关像素，即 DIP 或 CSS pixel)
	 */
	private String dvh;
	/**
	 * y 横竖屏 0 – 竖屏 1– 横屏
	 */
	private String orientation;
	/**
	 * y 设备生产商
	 */
	private String vendor;
	/**
	 * y 设备型号
	 */
	private String model;
	/**
	 * y 目前使用的语言-国 家 zh-CN
	 * 
	 */
	private String lan = "zh-CN";
	/**
	 * R iOS 设备是否越狱或 者Android设备是否 ROOT。1--是, 0--否/ 未知(默认)
	 * 
	 */
	private String brk;
	/**
	 * R 地理位置 经纬度
	 * 
	 */
	private String geo;
	/**
	 * R Wifi SSID
	 */
	private String ssid;
	/**
	 * Y 是否开屏 1，表示开屏；0 表示非开屏
	 * 
	 */
	private String isboot;
	/**
	 * y 请求批量下发广告 的数量 Y for native AD 目前只能为”1”
	 */
	private String batch_cnt = "1";
	/**
	 * R 调试
	 */
	private Debug debug = new Debug();

	/**
	 * y appid（与在讯飞 后台注册的 appid 保持一致）
	 */
	private String appid;
	/**
	 * y App Name（与在 讯飞后台注册的 应用名称保持一 致）
	 * 
	 */
	private String appname = "车来了";
	/**
	 * Y App 自身的版本 号
	 * 
	 */
	private String appver;
	/**
	 * y APP 应用的包名 称（与在讯飞后 台注册的应用包 名保持一致）
	 * 
	 */
	private String pkgname;

	/**
	 * y 讯飞广告平台注册广告位 ID
	 */
	private String adunitid;

	/**
	 * y 协议号 1.3.8
	 */
	private String api_ver = "1.3.8";

	/**
	 * y 媒体支持的http 协议类型
	 * 
	 * 1 - 只支持 http 2 - 只支持 https 3 - https 和 http 都支 持
	 * 
	 */
	private int secure = 3;

	/**
	 * Recommended 下游需要的普 通广告的物料 格式(只对普通 广告生效)，在 物料下发时候 一次请求只会 下发其中的一 种格式的物料 json
	 * 或者html
	 * 
	 * 取值范围， html:下游只支 html； json:下游只支持 json； htmlorjson:下游同时 支持 html 和 json。 默认为
	 * html，如果想要 除了 html 外其他的格 式的就需要填写
	 * 
	 */
	private String tramaterialtype = "json";

	public String getDevicetype() {
		return devicetype;
	}

	public void setDevicetype(String devicetype) {
		this.devicetype = devicetype;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getOsv() {
		return osv;
	}

	public void setOsv(String osv) {
		this.osv = osv;
	}

	public String getOpenudid() {
		return openudid;
	}

	public void setOpenudid(String openudid) {
		this.openudid = openudid;
	}

	public String getOpenudidsha1() {
		return openudidsha1;
	}

	public void setOpenudidsha1(String openudidsha1) {
		this.openudidsha1 = openudidsha1;
	}

	public String getOpenudidmd5() {
		return openudidmd5;
	}

	public void setOpenudidmd5(String openudidmd5) {
		this.openudidmd5 = openudidmd5;
	}

	public String getAdid() {
		return adid;
	}

	public void setAdid(String adid) {
		this.adid = adid;
	}

	public String getAdidsha1() {
		return adidsha1;
	}

	public void setAdidsha1(String adidsha1) {
		this.adidsha1 = adidsha1;
	}

	public String getAdidmd5() {
		return adidmd5;
	}

	public void setAdidmd5(String adidmd5) {
		this.adidmd5 = adidmd5;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getImeisha1() {
		return imeisha1;
	}

	public void setImeisha1(String imeisha1) {
		this.imeisha1 = imeisha1;
	}

	public String getImeimd5() {
		return imeimd5;
	}

	public void setImeimd5(String imeimd5) {
		this.imeimd5 = imeimd5;
	}

	public String getIdfa() {
		return idfa;
	}

	public void setIdfa(String idfa) {
		this.idfa = idfa;
	}

	public String getIdfv() {
		return idfv;
	}

	public void setIdfv(String idfv) {
		this.idfv = idfv;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getMacsha1() {
		return macsha1;
	}

	public void setMacsha1(String macsha1) {
		this.macsha1 = macsha1;
	}

	public String getMacmd5() {
		return macmd5;
	}

	public void setMacmd5(String macmd5) {
		this.macmd5 = macmd5;
	}

	public String getAaid() {
		return aaid;
	}

	public void setAaid(String aaid) {
		this.aaid = aaid;
	}

	public String getDuid() {
		return duid;
	}

	public void setDuid(String duid) {
		this.duid = duid;
	}

	public String getDensity() {
		return density;
	}

	public void setDensity(String density) {
		this.density = density;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getNet() {
		return net;
	}

	public void setNet(String net) {
		this.net = net;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getUa() {
		return ua;
	}

	public void setUa(String ua) {
		this.ua = ua;
	}

	public String getTs() {
		return ts;
	}

	public void setTs(String ts) {
		this.ts = ts;
	}

	public String getAdw() {
		return adw;
	}

	public void setAdw(String adw) {
		this.adw = adw;
	}

	public String getAdh() {
		return adh;
	}

	public void setAdh(String adh) {
		this.adh = adh;
	}

	public String getDvw() {
		return dvw;
	}

	public void setDvw(String dvw) {
		this.dvw = dvw;
	}

	public String getDvh() {
		return dvh;
	}

	public void setDvh(String dvh) {
		this.dvh = dvh;
	}

	public String getOrientation() {
		return orientation;
	}

	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getLan() {
		return lan;
	}

	public void setLan(String lan) {
		this.lan = lan;
	}

	public String getBrk() {
		return brk;
	}

	public void setBrk(String brk) {
		this.brk = brk;
	}

	public String getGeo() {
		return geo;
	}

	public void setGeo(String geo) {
		this.geo = geo;
	}

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public String getIsboot() {
		return isboot;
	}

	public void setIsboot(String isboot) {
		this.isboot = isboot;
	}

	public String getBatch_cnt() {
		return batch_cnt;
	}

	public void setBatch_cnt(String batch_cnt) {
		this.batch_cnt = batch_cnt;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getAppname() {
		return appname;
	}

	public void setAppname(String appname) {
		this.appname = appname;
	}

	public String getAppver() {
		return appver;
	}

	public void setAppver(String appver) {
		this.appver = appver;
	}

	public String getPkgname() {
		return pkgname;
	}

	public void setPkgname(String pkgname) {
		this.pkgname = pkgname;
	}

	public String getAdunitid() {
		return adunitid;
	}

	public void setAdunitid(String adunitid) {
		this.adunitid = adunitid;
	}

	public String getApi_ver() {
		return api_ver;
	}

	public void setApi_ver(String api_ver) {
		this.api_ver = api_ver;
	}

	public int getSecure() {
		return secure;
	}

	public void setSecure(int secure) {
		this.secure = secure;
	}

	public String getTramaterialtype() {
		return tramaterialtype;
	}

	public void setTramaterialtype(String tramaterialtype) {
		this.tramaterialtype = tramaterialtype;
	}

	public Debug getDebug() {
		return debug;
	}

	public void setDebug(Debug debug) {
		this.debug = debug;
	}

}

class Debug {
	/**
	 * 用于指定下发广告的交互类型， 取值范围：0，不限制； 1，跳 转类； 2，下载类；3，特殊下 载类（参见章节 2.3.6）。不指定 的话，按值为 0
	 * 处理。
	 * 
	 */
	private int action_type=2;
	/**
	 * 用于指定下发广告的落地页类 型，取值范围：0，不限制；1， 包含 landing_url 和 deep_link； 2，仅包含
	 * landing_url。不指定 的话，按值为 0 处理。
	 * 
	 */
	private int landing_ty=0;
	public int getAction_type() {
		return action_type;
	}
	public void setAction_type(int action_type) {
		this.action_type = action_type;
	}
	public int getLanding_ty() {
		return landing_ty;
	}
	public void setLanding_ty(int landing_ty) {
		this.landing_ty = landing_ty;
	}
	
	
}
