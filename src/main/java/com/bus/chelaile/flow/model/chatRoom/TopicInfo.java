/**
 * 
 */
/**
 * @author linzi
 *
 */
package com.bus.chelaile.flow.model.chatRoom;

import java.util.*;

import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.util.New;

public class TopicInfo {
	
	private String topicId;
	private String content;
	private String link;
	private String chatRoomId;
	private int chatTotal;
	private TemplateInfo template;
	private List<ChatUserInfo> comperes;
	private List<ChatUserInfo> managers;
	public String getTopicId() {
		return topicId;
	}
	public void setTopicId(String topicId) {
		this.topicId = topicId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getChatRoomId() {
		return chatRoomId;
	}
	public void setChatRoomId(String chatRoomId) {
		this.chatRoomId = chatRoomId;
	}
	public int getChatTotal() {
		return chatTotal;
	}
	public void setChatTotal(int chatTotal) {
		this.chatTotal = chatTotal;
	}
	public TemplateInfo getTemplate() {
		return template;
	}
	public void setTemplate(TemplateInfo template) {
		this.template = template;
	}
	public List<ChatUserInfo> getComperes() {
		return comperes;
	}
	public void setComperes(List<ChatUserInfo> comperes) {
		this.comperes = comperes;
	}
	public List<ChatUserInfo> getManagers() {
		return managers;
	}
	public void setManagers(List<ChatUserInfo> managers) {
		this.managers = managers;
	}
	@Override
	public String toString() {
		return "TopicInfo [topicId=" + topicId + ", content=" + content + ", link=" + link + ", chatRoomId="
				+ chatRoomId + ", chatTotal=" + chatTotal + ", template=" + template + ", comperes=" + comperes
				+ ", managers=" + managers + "]";
	}
	
	
	public static void main(String[] args) {
		
		TopicInfo topic = new TopicInfo();
		
		TemplateInfo template = new TemplateInfo();
//		template.setLink("http://image3.chelaile.net.cn/0e78083f368e4e86b08711e67b40712b");
		
		ChatUserInfo chat = new ChatUserInfo();
		chat.setChatUserId("1234567");
		chat.setNickname("主持人昵称");
		List<ChatUserInfo> comperes = New.arrayList();
		comperes.add(chat);
		
		topic.setChatRoomId("123456");
		topic.setChatTotal(382);
		topic.setContent("公交车应不应该给老人让座");
		topic.setLink("http://image3.chelaile.net.cn/0e78083f368e4e86b08711e67b40712b");
		topic.setComperes(comperes);
		topic.setTemplate(template);
		
		System.out.println(JSONObject.toJSONString(topic));
	}
}