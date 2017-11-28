package com.bus.chelaile.flow.model;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.flow.model.chatRoom.TopicInfo;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.util.AdvUtil;
import com.bus.chelaile.util.HttpUtils;

public class TabEntity {
	public static final String test_url = "http://test.chelaile.net.cn:7000/chatroom/app!getTopicById.action?";
	private static final String online_url = "http://100.98.112.14:7000/chatroom/app!getTopicById.action?";
	private static final Logger logger = LoggerFactory.getLogger(TabEntity.class);
	
	private int openType;
	private String link;
	private int id;
	private String pic; // 图片URL
	private int activityType;
	private int tagId;
	private String tag;
	private String feedId;
	private TopicInfo topic;
	
	
	
	public boolean fillActivityInfo(ActivityContent activityContent, AdvParam advParam, Map<String, String> paramMap) {
		if (activityContent == null || advParam == null) {
			return false;
		}

		if (activityContent.getType() == 4) { // 聊天室
			TopicInfo topicInfo = getTopicById(advParam, activityContent.getChat_room_id());
			if(topicInfo != null) {
				this.setTopic(topicInfo);
			} else {
				logger.error("查询得到的聊天室对象为空！ 请检查城市规则是否正确 , udid={}, Chat_room_id={}, cityId={}", 
						advParam.getUdid(), activityContent.getChat_room_id(), advParam.getCityId());
				return false;
			}
		}
		
		
		
		this.activityType = 1; // 信息流是活动的时候，type是1
		this.openType = activityContent.getOpen_type() - 1;  // 接口 0 内部打开，数据库 1 内部打开
		this.id = activityContent.getActivity_id();
		this.tag = activityContent.getTag_title();

		this.setActivityType(activityContent.getType());
		this.setPic(activityContent.getPic());
		this.setLink(AdvUtil.buildRedirectLink(activityContent.getLink(), paramMap, advParam.getUdid(), null,
				null, false, true, 1));
		this.setTagId(activityContent.getTag_id());
		this.setTag(activityContent.getTag_name());
		this.setFeedId(activityContent.getFeed_id());


		fillActivityLink(activityContent, advParam, paramMap); // 填充content的pic
															// 和 title
		return true;

	}
	
	/*
	 * 调用聊天室后台，获取聊天室对象
	 */
	private TopicInfo getTopicById(AdvParam advParam, int chat_room_id) {

		TopicInfo topic = new TopicInfo();
		String url = null;
		if (Constants.ISTEST) {
			url = test_url + "cityId=" + advParam.getCityId() + "&chatRoomId=" + chat_room_id;
		} else {
			url = online_url + "cityId=" + advParam.getCityId() + "&chatRoomId=" + chat_room_id;
		}

		String response = null;
		try {
			response = HttpUtils.get(url, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("获取信息流对象出错, udid={}, url={}, response={}", advParam.getUdid(), url, response);
			return null;
		}
		if (response == null) {
			return null;
		}

		try {
			String responseJ = response.substring(6, response.length() - 6);
			JSONObject responseJSON = JSON.parseObject(responseJ);
			topic = JSON.toJavaObject(responseJSON.getJSONObject("jsonr").getJSONObject("data").getJSONObject("topic"),
					TopicInfo.class);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("获取信息流对象出错，返回对象转json出错，udid={}, resposne={}", advParam.getUdid(), response);
			return null;
		}
		if(topic == null || topic.getTopicId() == null) {
			logger.error("获取信息流对象为空， udid={}, url={}, response={}", advParam.getUdid(), url, response);
			return null;
		}
		return topic;
	}
	
	private void fillActivityLink(ActivityContent activityContent, AdvParam advParam, Map<String, String> paramMap) {
		this.link = AdvUtil.buildRedirectLink(activityContent.getLink(), paramMap, advParam.getUdid(), null,
				null, false, true, 1);
	}
	
	
	
	
	public int getOpenType() {
		return openType;
	}
	public void setOpenType(int openType) {
		this.openType = openType;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPic() {
		return pic;
	}
	public void setPic(String pic) {
		this.pic = pic;
	}
	public int getActivityType() {
		return activityType;
	}
	public void setActivityType(int activityType) {
		this.activityType = activityType;
	}
	public int getTagId() {
		return tagId;
	}
	public void setTagId(int tagId) {
		this.tagId = tagId;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getFeedId() {
		return feedId;
	}
	public void setFeedId(String feedId) {
		this.feedId = feedId;
	}
	public TopicInfo getTopic() {
		return topic;
	}
	public void setTopic(TopicInfo topic) {
		this.topic = topic;
	}
}
