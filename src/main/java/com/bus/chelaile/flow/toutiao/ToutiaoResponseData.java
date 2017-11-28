package com.bus.chelaile.flow.toutiao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.model.Platform;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.flow.model.Thumbnail;
import com.bus.chelaile.flow.model.FlowContent;
import com.bus.chelaile.util.AdvUtil;
import com.bus.chelaile.util.New;

public class ToutiaoResponseData {
	
	private static final Logger logger = LoggerFactory.getLogger(ToutiaoResponseData.class);
	
	private boolean has_video; //是否是视频
	private long group_id; //新闻id，本字段在本地调起、点击上报时使用。
	private long ad_id; //广告id
	private long item_id; //本字段在dislike上报时使用。
	private String tag; //新闻所属频道、
	private String label; // 细粒度的文章标签, 当label==‘广告’ 时, 当前为广告。
	private String log_extra; //广告特有字段
	private String title; //新闻title
	private String source; //新闻来源
	private String article_url;// 新闻链接
	private long publish_time; //新闻发布时间
	private long behot_time; //时间戳，此时间在refresh或load more时使用
	private List<Thumbnail> large_image_list;	//大图
	private Thumbnail middle_image;		//右图
	private List<Thumbnail> image_list;		//三图
//	private String abstract; //新闻简介
	
	public long getGroup_id() {
		return group_id;
	}
	public void setGroup_id(long group_id) {
		this.group_id = group_id;
	}
	public long getItem_id() {
		return item_id;
	}
	public void setItem_id(long item_id) {
		this.item_id = item_id;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getArticle_url() {
		return article_url;
	}
	public void setArticle_url(String article_url) {
		this.article_url = article_url;
	}
	public long getPublish_time() {
		return publish_time;
	}
	public void setPublish_time(long publish_time) {
		this.publish_time = publish_time;
	}
	public long getBehot_time() {
		return behot_time;
	}
	public void setBehot_time(long behot_time) {
		this.behot_time = behot_time;
	}
	public List<Thumbnail> getLarge_image_list() {
		return large_image_list;
	}
	public void setLarge_image_list(List<Thumbnail> large_image_list) {
		this.large_image_list = large_image_list;
	}
	public Thumbnail getMiddle_image() {
		return middle_image;
	}
	public void setMiddle_image(Thumbnail middle_image) {
		this.middle_image = middle_image;
	}
	public List<Thumbnail> getImage_list() {
		return image_list;
	}
	public void setImage_list(List<Thumbnail> image_list) {
		this.image_list = image_list;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getLog_extra() {
		return log_extra;
	}
	public void setLog_extra(String log_extra) {
		this.log_extra = log_extra;
	}
	@Override
	public String toString() {
		return "ToutiaoResponseData [group_id=" + group_id + ", item_id=" + item_id + ", tag=" + tag + ", title="
				+ title + ", source=" + source + ", article_url=" + article_url + ", publish_time=" + publish_time
				+ ", behot_time=" + behot_time + ", large_image_list=" + large_image_list + ", middle_image="
				+ middle_image + ", image_list=" + image_list + "]";
	}
	
	/*
	 * 根据返回头条结构体 生成 给客户端的 content
	 */
	public FlowContent dealDate(AdvParam advParam, String channelId) {
		FlowContent content = new FlowContent();
		content.setId(String.valueOf(this.group_id));
		content.setTitle(this.title);
		content.setTime(this.behot_time * 1000);	//此处与uc不一样，不能用发布时间
		content.setDesc(this.source);
		content.setRecoid(String.valueOf(this.item_id));
		content.setUrl(delUrl(advParam, channelId));
		
		// 将图片https改成http
		for(Thumbnail t : large_image_list) {
			t.setUrl(t.getUrl().replace("http", "https"));
		}
		for(Thumbnail t : image_list) {
			t.setUrl(t.getUrl().replace("http", "https"));
		}
		
		// 根据返回的图片结构构建自己的图片
		if(large_image_list != null && large_image_list.size() >= 1) {
			content.setImgs((ArrayList<Thumbnail>) large_image_list);
			content.setImgsType(1);	//单图，大图
		}
		else if(image_list != null && image_list.size() >=3 ) {
			content.setImgs((ArrayList<Thumbnail>) image_list);
			content.setImgsType(2);	//三图，小图
		} else if(middle_image != null && middle_image.getUrl() != null) {
			middle_image.setUrl(middle_image.getUrl().replace("http", "https"));  // TODO
			List<Thumbnail> img = New.arrayList();
			img.add(middle_image);
			content.setImgs((ArrayList<Thumbnail>) img);
			content.setImgsType(0);	//单图，小图
		}
			
		return content;
	}
	
	private String delUrl(AdvParam advParam, String channelId) {
		HashMap<String, String> paramsMap = New.hashMap();
		String udid = advParam.getUdid();
		String category = "open";
		String tag = "go_detail";
		String label1 = "click_" + (channelId.equals("__all__") ? "headline" : channelId);
		String pdid = advParam.getUdid();
		Platform platform = Platform.from(advParam.getS());
		if(platform.isIOS(advParam.getS())) {
			pdid = advParam.getIdfa();
		}
		
		
		paramsMap.put("udid", udid);
		paramsMap.put("category", category);
		paramsMap.put("tag", tag);
		paramsMap.put("label", label1);
		paramsMap.put("value", String.valueOf(this.group_id));
		paramsMap.put("gse", "1");		//放在最外层的参数，未编码进link参数里面，如果跳转是ad，最终页面会丢失这个参数
		paramsMap.put("wse", "1");	
		
		if (this.label != null && this.label.equals("广告")) { 	//广告
			tag = "embeded_ad";
			String is_ad_event = "1";
			label1 = "click";
			String nt = advParam.getNw();	// 请求参数nw的值，上报的时候，需要转换
			
			paramsMap.put("value", String.valueOf(this.ad_id));
			paramsMap.put("ua", advParam.getUa());
			paramsMap.put("pdid", pdid);
			paramsMap.put("device_type", advParam.getDeviceType());	// 请求参数deviceType的值，上报的时候，需要转换
			paramsMap.put("tag", tag);
			paramsMap.put("is_ad_event", is_ad_event);
			paramsMap.put("label", label1);
			paramsMap.put("nt", String.valueOf(nt));
			paramsMap.put("log_extra", AdvUtil.encodeUrl(this.getLog_extra()));
			paramsMap.put("ip", advParam.getIp());
			
			logger.info("构建广告上报事件：ip={}, title={}, id={}", advParam.getIp(), this.title, this.group_id);
			System.out.println("构建广告上报事件：ip=" + advParam.getIp());
			
			logger.info("构建广告 点击链接: paramsMap={}", JSONObject.toJSONString(paramsMap));
			System.out.println("广告！" + JSONObject.toJSONString(paramsMap));
		} else {
//			logger.info("构建普通文章点击链接: paramsMap={}", JSONObject.toJSONString(paramsMap));
		}
		
		String urlHttps = this.article_url;
		if(! this.article_url.contains("https")){
			urlHttps = this.article_url.replace("http", "https");	//  改成https
		}
		urlHttps += "&linkRefer=direct";
		
		return AdvUtil.buildRedirectLink(urlHttps, paramsMap, advParam.getUdid(), null,
				null, false, true, 0);	//参数1，决定使用redirect； 0，决定使用ad
	}
	public long getAd_id() {
		return ad_id;
	}
	public void setAd_id(long ad_id) {
		this.ad_id = ad_id;
	}
	public boolean isHas_video() {
		return has_video;
	}
	public void setHas_video(boolean has_video) {
		this.has_video = has_video;
	}
}
