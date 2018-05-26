package com.bus.chelaile.model.ads.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.bus.chelaile.model.ShowType;


/**
 * 详情页下方feed流广告，返回广告体
 * @author Administrator
 *
 */
public class LineFeedAdEntity extends FeedAdEntity {
	
	private String pic; // 图片URL
	
	@JSONField(serialize=false)
    private String title;
	@JSONField(serialize=false)
	private long autoInterval;
	@JSONField(serialize=false)
	private long mixInterval;
	
	private int apiType; // 原生or banner or 开屏等等
	
	private String subhead; //  上面的文字
	private String head; // 单图文章样式中，下面的文字
	
	// 构造方法
	public LineFeedAdEntity() {
	    this.showType = ShowType.LINE_FEED_ADV.getValue();
        this.pic = EMPTY_STR;
	}
	
	@Override
	protected ShowType gainShowTypeEnum() {
		return ShowType.LINE_FEED_ADV;
	}

	public String buildIdentity() {
        StringBuilder sb = new StringBuilder();
        sb.append("ADV[id=").append(id)
            .append("#showType=").append(showType)
            .append("#title=").append((getTitle() != null && getTitle().length() > 10) ? getTitle().substring(0, 10) : getTitle())
            .append("]");
        
        return sb.toString();
    }

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}


	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

    /**
     * @return the autoInterval
     */
    public long getAutoInterval() {
        return autoInterval;
    }

    /**
     * @param autoInterval the autoInterval to set
     */
    public void setAutoInterval(long autoInterval) {
        this.autoInterval = autoInterval;
    }

    /**
     * @return the mixInterval
     */
    public long getMixInterval() {
        return mixInterval;
    }

    /**
     * @param mixInterval the mixInterval to set
     */
    public void setMixInterval(long mixInterval) {
        this.mixInterval = mixInterval;
    }

    /**
     * @return the apiType
     */
    public int getApiType() {
        return apiType;
    }

    /**
     * @param apiType the apiType to set
     */
    public void setApiType(int apiType) {
        this.apiType = apiType;
    }

    /**
     * @return the subhead
     */
    public String getSubhead() {
        return subhead;
    }

    /**
     * @param subhead the subhead to set
     */
    public void setSubhead(String subhead) {
        this.subhead = subhead;
    }

    /**
     * @return the head
     */
    public String getHead() {
        return head;
    }

    /**
     * @param head the head to set
     */
    public void setHead(String head) {
        this.head = head;
    }
}
