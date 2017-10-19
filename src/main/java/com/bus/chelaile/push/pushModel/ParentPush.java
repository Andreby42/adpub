package com.bus.chelaile.push.pushModel;

import java.util.List;
/**
 * push	父类
 * @author zzz
 *
 */
public  class ParentPush {

	protected String expiredTime;
	protected String drives;
	protected String msgBody;
	protected List<String> clients;
	public String getExpiredTime() {
		return expiredTime;
	}
	public void setExpiredTime(String expiredTime) {
		this.expiredTime = expiredTime;
	}
	public String getDrives() {
		return drives;
	}
	public void setDrives(String drives) {
		this.drives = drives;
	}
	public String getMsgBody() {
		return msgBody;
	}
	public void setMsgBody(String msgBody) {
		this.msgBody = msgBody;
	}
	public List<String> getClients() {
		return clients;
	}
	public void setClients(List<String> clients) {
		this.clients = clients;
	}
	
	

}
