package com.bus.chelaile.flow.model.chatRoom;

public class ChatUserInfo {
	
	private String nickname;
	private String chatUserId;
	private String photoUrl;
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getChatUserId() {
		return chatUserId;
	}
	public void setChatUserId(String chatUserId) {
		this.chatUserId = chatUserId;
	}
	@Override
	public String toString() {
		return "ChatUserInfo [nickname=" + nickname + ", chatUserId=" + chatUserId + "]";
	}
	public String getPhotoUrl() {
		return photoUrl;
	}
	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}
}
