package com.bus.chelaile.flowNew.customContent;

public class RewardElement {
	private int rewardId;
	private String accountId;
	private long rewardTime;

	public int getRewardId() {
		return rewardId;
	}
	public void setRewardId(int rewardId) {
		this.rewardId = rewardId;
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
    public long getRewardTime() {
        return rewardTime;
    }
    public void setRewardTime(long rewardTime) {
        this.rewardTime = rewardTime;
    }
	@Override
	public String toString() {
		return "RewardElement [rewardId=" + rewardId + ", accountId=" + accountId + ", rewardTime=" + rewardTime + "]";
	}
    
    
}
