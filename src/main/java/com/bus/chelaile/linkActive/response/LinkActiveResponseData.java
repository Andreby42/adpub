/**
 * 
 */
/**
 * @author linzi
 *
 */
package com.bus.chelaile.linkActive.response;

public class LinkActiveResponseData {
	
	private String ad_code;
	private String ad_position;
	private String uri_scheme;	 //  广告主App的uri_scheme，通过此值来唤起App 
	private String pkg_name;  //广告主App的包名，用来判断广告主App是否安装了,仅android使用
	private String appstore_url;
	private String img_url;
	private String h5_url;	//广告内容页的h5地址或者引导用户下载的页面地址
	private String apk_url;	 //apk包的下载地址 ,仅android用
	private String active_device_type;
	private LinkActiveResponseAdContent ad_content;
	private int ad_content_id;
	private String request_id;
	public String getAd_code() {
		return ad_code;
	}
	public void setAd_code(String ad_code) {
		this.ad_code = ad_code;
	}
	public String getAd_position() {
		return ad_position;
	}
	public void setAd_position(String ad_position) {
		this.ad_position = ad_position;
	}
	public String getUri_scheme() {
		return uri_scheme;
	}
	public void setUri_scheme(String uri_scheme) {
		this.uri_scheme = uri_scheme;
	}
	public String getAppstore_url() {
		return appstore_url;
	}
	public void setAppstore_url(String appstore_url) {
		this.appstore_url = appstore_url;
	}
	public String getImg_url() {
		return img_url;
	}
	public void setImg_url(String img_url) {
		this.img_url = img_url;
	}
	public String getH5_url() {
		return h5_url;
	}
	public void setH5_url(String h5_url) {
		this.h5_url = h5_url;
	}
	public String getApk_url() {
		return apk_url;
	}
	public void setApk_url(String apk_url) {
		this.apk_url = apk_url;
	}
	public String getActive_device_type() {
		return active_device_type;
	}
	public void setActive_device_type(String active_device_type) {
		this.active_device_type = active_device_type;
	}
	public String getRequest_id() {
		return request_id;
	}
	public void setRequest_id(String request_id) {
		this.request_id = request_id;
	}
	public LinkActiveResponseAdContent getAd_content() {
		return ad_content;
	}
	public void setAd_content(LinkActiveResponseAdContent ad_content) {
		this.ad_content = ad_content;
	}
	public String getPkg_name() {
		return pkg_name;
	}
	public void setPkg_name(String pkg_name) {
		this.pkg_name = pkg_name;
	}
	public int getAd_content_id() {
		return ad_content_id;
	}
	public void setAd_content_id(int ad_content_id) {
		this.ad_content_id = ad_content_id;
	}
}
