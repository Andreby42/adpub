package com.bus.chelaile.model.ads.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.bus.chelaile.model.ShowType;

public class LineRightAdEntity extends BaseAdEntity {
	private String pic;
//	private Tag tag;
//	private String feedId;
	@JSONField(serialize = false)
    private long autoInterval;
    @JSONField(serialize = false)
    private long mixInterval;

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	// 构造方法
	public LineRightAdEntity() {
		super(ShowType.LINE_RIGHT_ADV.getValue());
		this.pic = EMPTY_STR;
	}

	@Override
	protected ShowType gainShowTypeEnum() {
		return ShowType.H5_LINEBANNER_ADV;
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

//	public Tag getTag() {
//		return tag;
//	}
//
//	public void setTag(Tag tag) {
//		this.tag = tag;
//	}
//
//	public String getFeedId() {
//		return feedId;
//	}
//
//	public void setFeedId(String feedId) {
//		this.feedId = feedId;
//	}
}
