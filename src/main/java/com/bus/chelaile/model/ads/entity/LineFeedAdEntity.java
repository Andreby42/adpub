package com.bus.chelaile.model.ads.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.bus.chelaile.model.ShowType;


/**
 * 详情页下方feed流广告，返回广告体
 * @author Administrator
 *
 */
public class LineFeedAdEntity extends BaseAdEntity {
	
	private String pic; // 图片URL
	
	@JSONField(serialize=false)
	private int adWeight;    // 轮播权重
	@JSONField(serialize=false)
    private String title;
	@JSONField(serialize=false)
	private long autoInterval;
	@JSONField(serialize=false)
	private long mixInterval;
	
	private int apitype; // 原生or banner or 开屏等等
	
	// 构造方法
	public LineFeedAdEntity() {
        super(ShowType.LINE_FEED_ADV.getValue());
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

	
	public int getAdWeight() {
		return adWeight;
	}

	public void setAdWeight(int adWeight) {
		this.adWeight = adWeight;
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
     * @return the apitype
     */
    public int getApitype() {
        return apitype;
    }

    /**
     * @param apitype the apitype to set
     */
    public void setApitype(int apitype) {
        this.apitype = apitype;
    }
}
