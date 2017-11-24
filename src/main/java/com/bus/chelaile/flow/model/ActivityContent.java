package com.bus.chelaile.flow.model;

public class ActivityContent {
 
	private int activity_id;
	private int type;
	private String title;
	private String tag_title;
	private String tag_color;
	private String pic;
	private int tag_id;
	private String tag_name;
	private String link;
	private String feed_id;		//feed id，string类型
	private int chat_room_id;
	private int sort_index;
	private int status;
	private String rule;
	private String custom_channel;	//渠道名
	private int custom_channel_id; //渠道id
	private int channelType;	//渠道类型
	private int isShowInRecommend; //是否在推荐频道展示
	private int entry_id; // 入口位置
	private int open_type; // 打开方式,0 app内部，1 外部浏览器
	 
	
	
	public int getActivity_id() {
		return activity_id;
	}
	public void setActivity_id(int activity_id) {
		this.activity_id = activity_id;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTag_title() {
		return tag_title;
	}
	public void setTag_title(String tag_title) {
		this.tag_title = tag_title;
	}
	public String getTag_color() {
		return tag_color;
	}
	public void setTag_color(String tag_color) {
		this.tag_color = tag_color;
	}
	public String getPic() {
		return pic;
	}
	public void setPic(String pic) {
		this.pic = pic;
	}
	public int getTag_id() {
		return tag_id;
	}
	public void setTag_id(int tag_id) {
		this.tag_id = tag_id;
	}
	public String getTag_name() {
		return tag_name;
	}
	public void setTag_name(String tag_name) {
		this.tag_name = tag_name;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getFeed_id() {
		return feed_id;
	}
	public void setFeed_id(String feed_id) {
		this.feed_id = feed_id;
	}
	public int getChat_room_id() {
		return chat_room_id;
	}
	public void setChat_room_id(int chat_room_id) {
		this.chat_room_id = chat_room_id;
	}
	public int getSort_index() {
		return sort_index;
	}
	public void setSort_index(int sort_index) {
		this.sort_index = sort_index;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getRule() {
		return rule;
	}
	public void setRule(String rule) {
		this.rule = rule;
	}
	@Override
	public String toString() {
		return "ActivityContent [activity_id=" + activity_id + ", type=" + type + ", title=" + title + ", tag_title="
				+ tag_title + ", tag_color=" + tag_color + ", pic=" + pic + ", tag_id=" + tag_id + ", tag_name="
				+ tag_name + ", link=" + link + ", feed_id=" + feed_id + ", chat_room_id=" + chat_room_id
				+ ", sort_index=" + sort_index + ", status=" + status + ", rule=" + rule + "]";
	}
	public String getCustom_channel() {
		return custom_channel;
	}
	public void setCustom_channel(String custom_channel) {
		this.custom_channel = custom_channel;
	}
	public int getChannelType() {
		return channelType;
	}
	public void setChannelType(int channelType) {
		this.channelType = channelType;
	}
	public int getCustom_channel_id() {
		return custom_channel_id;
	}
	public void setCustom_channel_id(int custom_channel_id) {
		this.custom_channel_id = custom_channel_id;
	}
	public int getIsShowInRecommend() {
		return isShowInRecommend;
	}
	public void setIsShowInRecommend(int isShowInRecommend) {
		this.isShowInRecommend = isShowInRecommend;
	}
	public int getEntry_id() {
		return entry_id;
	}
	public void setEntry_id(int entry_id) {
		this.entry_id = entry_id;
	}
	public int getOpen_type() {
		return open_type;
	}
	public void setOpen_type(int open_type) {
		this.open_type = open_type;
	}
}
