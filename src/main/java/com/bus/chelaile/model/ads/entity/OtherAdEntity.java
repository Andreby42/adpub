package com.bus.chelaile.model.ads.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.bus.chelaile.model.ShowType;

/**
 * 换乘，更多车辆，站点对应线路
 *
 */
public class OtherAdEntity extends BaseAdEntity {

    public OtherAdEntity(int showType) {
        super(showType);
    }
    
    private ShowType stype;

    private String pic; // 图片URL

    @JSONField(serialize = false)
    private String title;
    @JSONField(serialize = false)
    private long autoInterval;
    @JSONField(serialize = false)
    private long mixInterval;

    private String subhead; //  上面的文字
    private String head; // 单图文章样式中，下面的文字
    private int imgsType; // 图片样式  0小图， 1 大图， 2 信息流专用右图小图样式

    private String action; //跳转信息流使用的字段

    // 构造方法
    //	public LineFeedAdEntity() {
    //	    this.showType = ShowType.LINE_FEED_ADV.getValue();
    //        this.pic = EMPTY_STR;
    //	}

    @Override
    protected ShowType gainShowTypeEnum() {
        return stype;
    }

    public String buildIdentity() {
        StringBuilder sb = new StringBuilder();
        sb.append("ADV[id=").append(id).append("#showType=").append(showType).append("#title=")
                .append((getTitle() != null && getTitle().length() > 10) ? getTitle().substring(0, 10) : getTitle()).append("]");

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

    /**
     * @return the imgsType
     */
    public int getImgsType() {
        return imgsType;
    }

    /**
     * @param imgsType the imgsType to set
     */
    public void setImgsType(int imgsType) {
        this.imgsType = imgsType;
    }

    /**
     * @return the action
     */
    public String getAction() {
        return action;
    }

    /**
     * @param action the action to set
     */
    public void setAction(String action) {
        this.action = action;
    }

	public ShowType getStype() {
		return stype;
	}

	public void setStype(ShowType stype) {
		this.stype = stype;
	}
    
    
    
}
