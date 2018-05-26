package com.bus.chelaile.model.ads.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.model.ads.Tag;




public class AdEntity extends BaseAdEntity{
    public AdEntity(int showType) {
		super(showType);
	}

	public static final String EMPTY = "";
    
    //private int id;           //广告的唯一编号
   // private int showType;
    
  //  private int openType;
    
    // 广告投放的位置
    private int sindex;
    private int lindex;
    private String brandIcon;
    private String brandName;
    private String promoteTitle;
    private int distance;
    
    // 以下字段，两种广告类型下都必须有值
    private String barColor;
    private String head;
    private String subhead;
    private int buttonType;
    private String buttonIcon;
    
    //如果buttonType==0，以下两个字段必须，否则为默认值空串
    private String buttonTitle;
    private String buttonColor;
	private Tag tag;
    private String feedId;
    
    private String desc;

 //   private int monitorType;
  //  private int type;
    
    @JSONField(serialize = false)
    private long autoInterval;
    @JSONField(serialize = false)
    private long mixInterval;

	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}

	public String getFeedId() {
		return feedId;
	}

	public void setFeedId(String feedId) {
		this.feedId = feedId;
	}

    public void convertNullFieldToEmpty() {
        if (link == null)
            link = EMPTY;
        if (brandIcon == null)
            brandIcon = EMPTY;
        if (brandName == null)
            brandName = EMPTY;
        if (promoteTitle == null)
            promoteTitle = EMPTY;
        if (barColor == null)
            barColor = EMPTY;
        if (head == null)
            head = EMPTY;
        if (subhead == null)
            subhead = EMPTY;
        if (buttonIcon == null)
            buttonIcon = EMPTY;
        if (buttonTitle == null)
            buttonTitle = EMPTY;
        if (buttonColor == null)
            buttonColor = EMPTY;
        if (unfoldMonitorLink == null)
            unfoldMonitorLink = EMPTY;
        if (clickMonitorLink == null)
            clickMonitorLink = EMPTY;
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getShowType() {
		return showType;
	}

	public void setShowType(int showType) {
		this.showType = showType;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public int getOpenType() {
		return openType;
	}

	public void setOpenType(int openType) {
		this.openType = openType;
	}

	public int getSindex() {
		return sindex;
	}

	public void setSindex(int sindex) {
		this.sindex = sindex;
	}

	public int getLindex() {
		return lindex;
	}

	public void setLindex(int lindex) {
		this.lindex = lindex;
	}

	public String getBrandIcon() {
		return brandIcon;
	}

	public void setBrandIcon(String brandIcon) {
		this.brandIcon = brandIcon;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getPromoteTitle() {
		return promoteTitle;
	}

	public void setPromoteTitle(String promoteTitle) {
		this.promoteTitle = promoteTitle;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public String getBarColor() {
		return barColor;
	}

	public void setBarColor(String barColor) {
		this.barColor = barColor;
	}

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getSubhead() {
		return subhead;
	}

	public void setSubhead(String subhead) {
		this.subhead = subhead;
	}

	public int getButtonType() {
		return buttonType;
	}

	public void setButtonType(int buttonType) {
		this.buttonType = buttonType;
	}

	public String getButtonIcon() {
		return buttonIcon;
	}

	public void setButtonIcon(String buttonIcon) {
		this.buttonIcon = buttonIcon;
	}

	public String getButtonTitle() {
		return buttonTitle;
	}

	public void setButtonTitle(String buttonTitle) {
		this.buttonTitle = buttonTitle;
	}

	public String getButtonColor() {
		return buttonColor;
	}

	public void setButtonColor(String buttonColor) {
		this.buttonColor = buttonColor;
	}



	public static String getEmpty() {
		return EMPTY;
	}


	@Override
	protected ShowType gainShowTypeEnum() {
		if (showType == ShowType.DOUBLE_COLUMN.getValue()) {
			return ShowType.DOUBLE_COLUMN;
		} else if (showType == ShowType.SINGLE_COLUMN.getValue()) {
			return ShowType.SINGLE_COLUMN;
		} else if (showType == ShowType.FLOW_ADV.getValue()) {
			return ShowType.FLOW_ADV;
		} else if (showType == ShowType.ROUTE_PLAN_ADV.getValue()) {
			return ShowType.ROUTE_PLAN_ADV;
		} else {
			return ShowType.DOUBLE_COLUMN;
		}
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
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
    
    
    
}
