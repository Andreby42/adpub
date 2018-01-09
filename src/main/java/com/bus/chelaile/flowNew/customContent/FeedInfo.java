package com.bus.chelaile.flowNew.customContent;

import java.util.Map;

public class FeedInfo {

	private FeedElement feed;
	private Map<String, AccountElement> accounts;
	
	public FeedElement getFeed() {
		return feed;
	}
	public void setFeed(FeedElement feed) {
		this.feed = feed;
	}
	public Map<String, AccountElement> getAccounts() {
		return accounts;
	}
	public void setAccounts(Map<String, AccountElement> accounts) {
		this.accounts = accounts;
	}
}
