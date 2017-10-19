package com.bus.chelaile.thread.model;

import com.bus.chelaile.model.QueueCacheType;
import com.bus.chelaile.model.record.AdPubCacheRecord;
import com.bus.chelaile.flow.model.ListIdsCache;
import com.bus.chelaile.flow.model.FlowContent;


public class QueueObject {

	private String key;		//缓存的 key值
	
	private AdPubCacheRecord adPubCacheRecord;
	
	private int time;	//存储 
	
	private String redisIncrKey ;	//需要自增的key,设定的是：该值与adpubCacheRecord互斥存在
	
	private ListIdsCache articleIds; // 用户当天展示过的articles结果提，key是  关键字加上用户id,关键比如 SHOWN# 和 BLOCK#
	
	private FlowContent ucContent; // 信息流文章内容，key是用户id
	
	private QueueCacheType queueType;	//QueueCacheType
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public AdPubCacheRecord getAdPubCacheRecord() {
		return adPubCacheRecord;
	}

	public void setAdPubCacheRecord(AdPubCacheRecord adPubCacheRecord) {
		this.adPubCacheRecord = adPubCacheRecord;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public String getRedisIncrKey() {
		return redisIncrKey;
	}

	public void setRedisIncrKey(String redisIncrKey) {
		this.redisIncrKey = redisIncrKey;
	}


	public FlowContent getUcContent() {
		return ucContent;
	}

	public void setUcContent(FlowContent ucContent) {
		this.ucContent = ucContent;
	}

	public QueueCacheType getQueueType() {
		return queueType;
	}

	public void setQueueType(QueueCacheType queueType) {
		this.queueType = queueType;
	}

	public ListIdsCache getArticleIds() {
		return articleIds;
	}

	public void setArticleIds(ListIdsCache articleIds) {
		this.articleIds = articleIds;
	}

}
