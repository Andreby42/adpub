package com.bus.chelaile.flow.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.flow.model.chatRoom.TopicInfo;
import com.bus.chelaile.flowNew.model.FlowNewContent;
import com.bus.chelaile.model.ads.AdContent;
import com.bus.chelaile.model.ads.entity.AdEntity;
import com.bus.chelaile.model.ads.entity.BaseAdEntity;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.util.AdvUtil;
import com.bus.chelaile.util.HttpUtils;

public class FlowContent {

	public static final String test_url = "http://test.chelaile.net.cn:7000/chatroom/app!getTopicById.action?";
	private static final String online_url = "http://100.98.112.14:7000/chatroom/app!getTopicById.action?";
	private static final Logger logger = LoggerFactory.getLogger(FlowContent.class);

	private int type; // 类型，0：文章，1：活动，2：广告
	private String url; // 文章链接
	private ArrayList<Thumbnail> imgs; //
	private int imgsType; // 图片类型，0：单图小图，1：单图大图，2：三图小图
	private String title;
	private String id; // id，三种类型都有id
	private String recoid; // 记录推荐批次
	private String item_type; // 文章类型
	private String desc; // 第二个tag //文章来源//聊天室人数
	private long time; // 第三个tag //文章抓取入库时间
	private String tag; // 活动或者广告的 tag
	private String tagColor; // 活动或者广告的 tag颜色
	private ActivityEntity activityEntity;
	private BaseAdEntity adEntity; // = new AdEntity(10);

	/*
	 * 填充活动页内容
	 * 返回值表明 是否填充成功
	 */
	public boolean fillActivityInfo(ActivityContent activityContent, AdvParam advParam, Map<String, String> paramMap) {
		if (activityContent == null || advParam == null) {
			return false;
		}

		this.activityEntity = new ActivityEntity();
		if (activityContent.getType() == 4) { // 聊天室
			TopicInfo topicInfo = getTopicById(advParam, activityContent.getChat_room_id());
			if(topicInfo != null) {
				this.activityEntity.setTopic(topicInfo);
				
				if(topicInfo.getChatTotal() != -1) {	// -1的时候不予显示
					this.desc = "在线人数 " + topicInfo.getChatTotal();
				}
			} else {
				logger.error("查询得到的聊天室对象为空！ 请检查城市规则是否正确 , udid={}, Chat_room_id={}, cityId={}", 
						advParam.getUdid(), activityContent.getChat_room_id(), advParam.getCityId());
				return false;
			}
		}
		
		
		
		this.type = 1; // 信息流是活动的时候，type是1
		this.title = activityContent.getTitle();
		if(activityContent.getActivity_id() == 68 || activityContent.getActivity_id() == 197) {	// 特殊处理，68的活动将类型设置为ADV！ TODO 
			this.type = 2;
			AdContent adContent = new AdContent();
			adContent.setLink(activityContent.getLink());
			adContent.setOpenType(0);	// 0: 内部打开， 1 外部打开
			fillAdvInfo(adContent, advParam, paramMap);
			this.title = null;
		}
		this.id = activityContent.getActivity_id() + "";
		this.tag = activityContent.getTag_title();
		this.tagColor = activityContent.getTag_color();
		this.imgsType = 1; // 单图大图

		this.activityEntity.setType(activityContent.getType());
		this.activityEntity.setImageUrl(activityContent.getPic());
		this.activityEntity.setLinkUrl(AdvUtil.buildRedirectLink(activityContent.getLink(), paramMap, advParam.getUdid(), null,
				null, false, true, 1));
		this.activityEntity.setTagId(activityContent.getTag_id());
		this.activityEntity.setTag(activityContent.getTag_name());
		this.activityEntity.setFeedId(activityContent.getFeed_id());


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
		ArrayList<Thumbnail> imgs = new ArrayList<Thumbnail>();
		imgs.add(new Thumbnail(activityContent.getPic()));
		this.setImgs(imgs);

//		this.url = activityContent.getLink(); // TODO 此处需要加上一些参数
		this.url = AdvUtil.buildRedirectLink(activityContent.getLink(), paramMap, advParam.getUdid(), null,
				null, false, true, 1);
	}

	/*
	 * 填充广告内容
	 */
	public void fillAdvInfo(AdContent adContent, AdvParam advParam, Map<String, String> paramMap) {
		if (adContent == null || advParam == null) {
			return;
		}

		this.adEntity = new AdEntity(10);
		this.type = 2; // 信息流是广告的时候，type是2
		this.setTitle(adContent.getTitle());
		this.id = adContent.getId() + "";
		this.tag = "广告"; // adContent.getTag_title();
		this.tagColor = "gray"; // adContent.getTag_color();

		this.adEntity.fillBaseInfo(adContent, advParam, paramMap);

		fillAdvLink(this.adEntity, advParam, paramMap); // 填充content的pic 和 title
	}

	private void fillAdvLink(BaseAdEntity adEntity, AdvParam advParam, Map<String, String> paramMap) {
		ArrayList<Thumbnail> imgs = new ArrayList<Thumbnail>();
		imgs.add(new Thumbnail("图片链接"));
		this.setImgs(imgs);

		this.url = adEntity.getLink(); // TODO 此处需要加上一些参数
	}
	
	/*
	 * 构建 信息流2.0版本的样式
	 */
	public FlowNewContent createNewContents() {
		FlowNewContent f = new FlowNewContent();
		f.setDestType(2);
		f.setFlowTitle(this.title);
		f.setFlowTag("热门文章");
		f.setFlowTagColor("52,152,219");
		f.setFlowIcon("https://image3.chelaile.net.cn/3cd56eeb33c8434daf2e17bdc9fde48d");
		f.setArticleUrl(this.url);
		return f;
	}
	
	/*
	 * 构建 信息流2.0版本的样式
	 */
	public FlowNewContent createFeeds() {
		FlowNewContent f = new FlowNewContent();
		f.setDestType(2);
		f.setFlowTitle(this.title);
		f.setFlowTag("大家都在看");
		f.setFlowTagColor("52,152,219");
		f.setFlowTagColor("255,193,7");
		f.setFlowDesc("2567人浏览");
		f.setLink(this.url);
		f.setPic(this.imgs.get(0).getUrl());
		return f;
	}
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getRecoid() {
		return recoid;
	}

	public void setRecoid(String recoid) {
		this.recoid = recoid;
	}

	public String getItem_type() {
		return item_type;
	}

	public void setItem_type(String item_type) {
		this.item_type = item_type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public ArrayList<Thumbnail> getImgs() {
		return imgs;
	}

	public void setImgs(ArrayList<Thumbnail> imgs) {
		this.imgs = imgs;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getTagColor() {
		return tagColor;
	}

	public void setTagColor(String tagColor) {
		this.tagColor = tagColor;
	}

	public ActivityEntity getActivityEntity() {
		return activityEntity;
	}

	public void setActivityEntity(ActivityEntity activityEntity) {
		this.activityEntity = activityEntity;
	}

	public BaseAdEntity getAdEntity() {
		return adEntity;
	}

	public void setAdEntity(BaseAdEntity adEntity) {
		this.adEntity = adEntity;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		FlowContent ucContent = (FlowContent) o;

		return id.equals(ucContent.id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public String toString() {
		return "UcContent [type=" + type + ", url=" + url + ", imgs=" + imgs + ", title=" + title + ", id=" + id
				+ ", recoid=" + recoid + ", item_type=" + item_type + ", desc=" + desc + ", time=" + time + ", tag="
				+ tag + ", tagColor=" + tagColor + ", activityEntity=" + activityEntity + ", adEntity=" + adEntity
				+ "]";
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public int getImgsType() {
		return imgsType;
	}

	public void setImgsType(int imgsType) {
		this.imgsType = imgsType;
	}
	
	public static void main(String[] args) throws ClientProtocolException, IOException {
		
		String url = "http://test.chelaile.net.cn:7000/chatroom/app!getTopicById.action?cityId=027&chatRoomId=123471";
		String response = HttpUtils.get(url, "utf-8");
		System.out.println(response);
	}
}
