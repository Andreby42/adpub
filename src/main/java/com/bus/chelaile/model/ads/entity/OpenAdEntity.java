package com.bus.chelaile.model.ads.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.model.ads.Tag;
import com.bus.chelaile.util.config.PropertiesUtils;


public class OpenAdEntity  extends BaseAdEntity{

	public OpenAdEntity(int showType) {
		super(showType);
	}

	


	//private int id; // 广告的唯一ID
	//private int showType; // 广告类型： 0 双栏，1 单栏，2 首页 ，3 推送

	//private int openType; // 打开方式：0 APP内部打开；1 浏览器打开；默认值为0
	private int duration; // 持续时间，单位秒：0 手动关闭； 最短2秒，最长6秒
	private long expire; // 广告的有效期, 单位秒。
	private String pic; // 图片URL
	//private int targetType;
	private int isSkip;
	private int isDisplay;
	private int isFullShow; //是否全屏展示广告，针对香港要隐藏下面的车来了logo。默认为0

	//private int monitorType;
	
	private String placementId = "";
	private Tag tag;
    private String feedId;
    
    @JSONField(serialize = false)
    private long timeout; // 超时时间
    @JSONField(serialize = false)
    private int adWeight; // 轮播权重
    

 //   private int monitorType;
  //  private int type;

	
	private final static int isApiSkip = Integer.parseInt(PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			"newfullscreenisApiSkip", "0"));
	private final static int isApiDisplay = Integer.parseInt(PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			"newfullscreenisApiDisplay", "0"));
	private final static int isApiDuration = Integer.parseInt(PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			"newfullscreenisApiDuration", "3"));
	

	public void convertNullFieldToEmpty() {
		if (link == null)
			link = super.EMPTY_STR;

		if (pic == null) {
			pic = super.EMPTY_STR;
		}

		if (unfoldMonitorLink == null)
			unfoldMonitorLink = super.EMPTY_STR;

		if (clickMonitorLink == null)
			clickMonitorLink = super.EMPTY_STR;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		if (obj instanceof OpenAdEntity) {
			OpenAdEntity fullAdEntity = (OpenAdEntity) obj;
			if (this.getId() == fullAdEntity.getId()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.getId();
		return result;
	}

	
	@Override
	protected ShowType gainShowTypeEnum() {
		if (showType == ShowType.OPEN_SCREEN.getValue()) {
			return ShowType.OPEN_SCREEN;
		} else if (showType == ShowType.FULL_SCREEN.getValue()) {
			return ShowType.FULL_SCREEN;
		}
		else if (showType == ShowType.FULL_SCREEN_RIDE.getValue()) {
			return ShowType.FULL_SCREEN_RIDE;
		} else {
			return ShowType.FULL_SCREEN_MOBIKE;
		}
	}

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
	
	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public int getIsSkip() {
		return isSkip;
	}

	public void setIsSkip(int isSkip) {
		this.isSkip = isSkip;
	}

	public int getIsDisplay() {
		return isDisplay;
	}

	public void setIsDisplay(int isDisplay) {
		this.isDisplay = isDisplay;
	}
	
	public void setApiIsSkip(){
		this.isSkip = isApiSkip;
	}
	
	
	public void setApiDisplay(){
		this.isDisplay = isApiDisplay;
	}
	public void setApiDuration(){
		this.duration = isApiDuration;
	}

	@Override
	public String toString() {
		return "OpenAdEntity(duration=" + duration + ", pic=" + pic + ", isSkip=" + isSkip + ", isDisplay="
				+ isDisplay + ", placementId=" + getPlacementId() + ", id=" + id + ", showType=" + showType + ", link="
				+ link + ", openType=" + openType + ", targetType=" + targetType + ", unfoldMonitorLink="
				+ unfoldMonitorLink + ", clickMonitorLink=" + clickMonitorLink + ", monitorType=" + monitorType + ")";
	}

	public String getPlacementId() {
		return placementId;
	}

	public void setPlacementId(String placementId) {
		this.placementId = placementId;
	}

	public long getExpire() {
		return expire;
	}

	public void setExpire(long expire) {
		this.expire = expire;
	}

	public int getIsFullShow() {
		return isFullShow;
	}

	public void setIsFullShow(int isFullShow) {
		this.isFullShow = isFullShow;
	}

    /**
     * @return the timeout
     */
    public long getTimeout() {
        return timeout;
    }

    /**
     * @param timeout the timeout to set
     */
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    /**
     * @return the adWeight
     */
    public int getAdWeight() {
        return adWeight;
    }

    /**
     * @param adWeight the adWeight to set
     */
    public void setAdWeight(int adWeight) {
        this.adWeight = adWeight;
    }

	
}
