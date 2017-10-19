package com.bus.chelaile.model.ads.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.util.IdGenerateUtil;






/**
 * 调用第三方线路详情返回的对象
 * 
 * @author zzz
 * 
 */
public class ApiLineEntity extends LineAdEntity {

    protected static final Logger logger = LoggerFactory.getLogger(ApiLineEntity.class);

	private String placementId = ""; // 否 无 暂时只对应广点通的placementId
	private int apiType; // 否 无 类型，1：原生广告，2：banner广告
	private String apiDes = ""; // 是 无 原生广告时候说明
	// private String provider_id=""; //广告商
	private String apiTitle = ""; // 是 无 原生广告时候title
	private String apiPubContent = ""; // 是 无 banner广告时候返回的内容，需要使用base64解码
	private String packageName;	// linkActive 的启动app的apkname
//	private String uri_scheme; // 	广告主App的uri_scheme，通过此值来唤起App
	

	public ApiLineEntity() {
		super();
	}
	
	public String getPlacementId() {
		return placementId;
	}

	public void setPlacementId(String placementId) {
		this.placementId = placementId;
	}

	public int getApiType() {
		return apiType;
	}

	public void setApiType(int apiType) {
		this.apiType = apiType;
	}

	public String getApiDes() {
		return apiDes;
	}

	public void setApiDes(String apiDes) {
		this.apiDes = apiDes;
	}

	public String getApiTitle() {
		return apiTitle;
	}

	public void setApiTitle(String apiTitle) {
		this.apiTitle = apiTitle;
	}

	public String getApiPubContent() {
		return apiPubContent;
	}

	public void setApiPubContent(String apiPubContent) {
		this.apiPubContent = apiPubContent;
	}

	private synchronized static int getApiId() {
		//apiId++;
		return IdGenerateUtil.generateId();
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
}
