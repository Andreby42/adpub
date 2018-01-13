package com.bus.chelaile.flowNew.customContent;

import java.util.List;
import java.util.Map;

public class FeedListInfo {

	private List<FeedElement> feeds;
	private Map<String, AccountElement> accounts;
	public List<FeedElement> getFeeds() {
		return feeds;
	}
	public void setFeeds(List<FeedElement> feeds) {
		this.feeds = feeds;
	}
	public Map<String, AccountElement> getAccounts() {
		return accounts;
	}
	public void setAccounts(Map<String, AccountElement> accounts) {
		this.accounts = accounts;
	}
}
