package com.bus.chelaile.flowNew.customContent;


public class CommentElement {
	private String commentId;
	private String accountId;
	private String content;
	private long publishTime;
	   /**
     * 回复评论id
     */
    private String arguedCommentId;
    /**
     * 回复人id
     */
    private String arguedAccountId;
    
    private int likeCount;
    
    private int isLike;
    
    private String likeId;
    
    public int getIsLike() {
        return isLike;
    }
    
    public void setIsLike(int isLike) {
        this.isLike = isLike;
    }

    public int getLikeCount() {
        return likeCount;
    }
    
    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
    
    public String getLikeId() {
        return likeId;
    }

    
    public void setLikeId(String likeId) {
        this.likeId = likeId;
    }

    public String getCommentId() {
		return commentId;
	}
	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public long getPublishTime() {
		return publishTime;
	}
	public void setPublishTime(long publishTime) {
		this.publishTime = publishTime;
	}
    
    public String getArguedCommentId() {
        return arguedCommentId;
    }
    
    public void setArguedCommentId(String arguedCommentId) {
        this.arguedCommentId = arguedCommentId;
    }
    
    public String getArguedAccountId() {
        return arguedAccountId;
    }
    
    public void setArguedAccountId(String arguedAccountId) {
        this.arguedAccountId = arguedAccountId;
    }
	
}
