package com.bus.chelaile.flowNew.customContent;

import java.util.List;

public class FeedElement {
	private String fid;
	private int tagId;
	private String tag;
	private int tagType; //0,线路级别；1站点级别
	private String tagColor; //tag颜色，RGBA值逗号分隔,例如“255,255,255,0.3”
	private String extra;
	private String content;
	private List<ImageElement> images;
	private String accountId;
	private long publishTime;
	private List<CommentElement> comments;
	private int commentNum; //评论总数量
	private List<LikeElement> likes;
	private int rewardNum;
	private List<RewardElement> rewards;
	private int likeNum;
	private int isLike;  // 0没有，1有
	private int isReward;  // 0没有，1有
	private int isTop; //置顶：0 不是，1是
	private int isSpecial;
	private long sort;  //排序
	
	
	public long getSort() {
		return sort;
	}
	public void setSort(long sort) {
		this.sort = sort;
	}
	public String getFid() {
		return fid;
	}
	public void setFid(String fid) {
		this.fid = fid;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public int getTagType() {
		return tagType;
	}
	public void setTagType(int tagType) {
		this.tagType = tagType;
	}
	public String getExtra() {
		return extra;
	}
	public void setExtra(String extra) {
		this.extra = extra;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
    public List<ImageElement> getImages() {
        return images;
    }
    
    public void setImages(List<ImageElement> images) {
        this.images = images;
    }
    public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public long getPublishTime() {
		return publishTime;
	}
	public void setPublishTime(long publishTime) {
		this.publishTime = publishTime;
	}
	public List<CommentElement> getComments() {
		return comments;
	}
	public void setComments(List<CommentElement> comments) {
		this.comments = comments;
	}
	public List<LikeElement> getLikes() {
		return likes;
	}
	public void setLikes(List<LikeElement> likes) {
		this.likes = likes;
	}
	public int getRewardNum() {
		return rewardNum;
	}
	public void setRewardNum(int rewardNum) {
		this.rewardNum = rewardNum;
	}
	public int getLikeNum() {
		return likeNum;
	}
	public void setLikeNum(int likeNum) {
		this.likeNum = likeNum;
	}
	public int getIsLike() {
		return isLike;
	}
	public void setIsLike(int isLike) {
		this.isLike = isLike;
	}
	public int getIsReward() {
		return isReward;
	}
	public void setIsReward(int isReward) {
		this.isReward = isReward;
	}
	public String getTagColor() {
		return tagColor;
	}
	public void setTagColor(String tagColor) {
		this.tagColor = tagColor;
	}
	public int getIsTop() {
		return isTop;
	}
	public void setIsTop(int isTop) {
		this.isTop = isTop;
	}
	public int getCommentNum() {
		return commentNum;
	}
	public void setCommentNum(int commentNum) {
		this.commentNum = commentNum;
	}
    public List<RewardElement> getRewards() {
        return rewards;
    }
    public void setRewards(List<RewardElement> rewards) {
        this.rewards = rewards;
    }
    
    public int getTagId() {
        return tagId;
    }
    
    public void setTagId(int tagId) {
        this.tagId = tagId;
    }
    
    @Override
	public String toString() {
		return "FeedElement [fid=" + fid + ", tagId=" + tagId + ", tag=" + tag + ", tagType=" + tagType + ", tagColor="
				+ tagColor + ", extra=" + extra + ", content=" + content + ", images=" + images + ", accountId="
				+ accountId + ", publishTime=" + publishTime + ", comments=" + comments + ", commentNum=" + commentNum
				+ ", likes=" + likes + ", rewardNum=" + rewardNum + ", rewards=" + rewards + ", likeNum=" + likeNum
				+ ", isLike=" + isLike + ", isReward=" + isReward + ", isTop=" + isTop + ", isSpecial=" + isSpecial
				+ "]";
	}
	public int getIsSpecial() {
        return isSpecial;
    }
    public void setIsSpecial(int isSpecial) {
        this.isSpecial = isSpecial;
    }
	
	
}
