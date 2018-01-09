package com.bus.chelaile.flowNew.customContent;

public class LikeElement {
	private String likeId;
	private String accountId;
	private long likeTime;

	public String getLikeId() {
		return likeId;
	}
	public void setLikeId(String likeId) {
		this.likeId = likeId;
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
    public long getLikeTime() {
        return likeTime;
    }
    public void setLikeTime(long likeTime) {
        this.likeTime = likeTime;
    }
	@Override
	public String toString() {
		return "LikeElement [likeId=" + likeId + ", accountId=" + accountId + ", likeTime=" + likeTime + "]";
	}
    
    
}
