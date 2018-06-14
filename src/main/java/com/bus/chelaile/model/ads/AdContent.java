package com.bus.chelaile.model.ads;


import org.codehaus.jackson.annotate.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.common.CacheUtil;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.util.AdvUtil;

import java.util.Date;


public class AdContent {
    protected static final Logger logger = LoggerFactory.getLogger(AdContent.class);
    private int id;
    private String showType;
    private String title;
    private String link;
    private int openType;
    private String content; //广告的内容
    private int status;
    private Date createTime;
    private Date modifyTime;
    private int targetType;
    private String unfoldMonitorLink;
    private String clickMonitorLink;
    private int monitorType;
    private int priority;	//优先级
    private int is_fullScreen;		// 是否全屏展示 isFullShow
    private int link_extra =1; //广告链接额外加参数是否保留 0不保留  1保留
    private String projectId; 

    @JsonIgnore
    private AdInnerContent innerContent;

    @JsonIgnore
    private AdInnerContent innerPicContent;

    @JsonIgnore
    private String encodedLink;
   
    @JsonIgnore
    private String identity;
    
    public AdInnerContent getInnerContent() {
        if (innerContent != null) {
            return innerContent;
        }
        if(ShowType.WECHATAPP_BANNER_ADV.getType().equals(showType) || ShowType.WECHAT_FULL_ADV.getType().equals(showType)) {
        	AdWXBannerInnerContent wxBannerInner = new AdWXBannerInnerContent();
        	wxBannerInner.setAndParseJson(content);
        	innerContent = wxBannerInner;
        }
        else if(ShowType.STATION_ADV.getType().equals(showType)) {		// 站点广告
        	AdStationlInnerContent stationInner = new AdStationlInnerContent();
        	stationInner.setAndParseJson(content);
        	innerContent = stationInner;
        }
        else if (ShowType.DOUBLE_COLUMN.getType().equals(showType) || ShowType.FLOW_ADV.getType().equals(showType) 
                || ShowType.ROUTE_PLAN_ADV.getType().equals(showType)) { // 双栏广告 || 信息流广告
            AdDoubleInnerContent dblInner = new AdDoubleInnerContent();
            dblInner.setAndParseJson(content);
            innerContent = dblInner;
        }
        else if (ShowType.SINGLE_COLUMN.getType().equals(showType)) { // 单栏广告 || 线路规划页广告
            AdSingleInnerContent singleInner = new AdSingleInnerContent();
            singleInner.setAndParseJson(content);
            innerContent = singleInner;
        }
        else if(ShowType.FEED_ADV.getType().equals(showType)) {  // feed流广告
        	AdFeedInnerContent feedInner = new AdFeedInnerContent();
        	feedInner.setAndParseJson(content);
        	innerContent = feedInner;
        	if(feedInner != null && feedInner.getLikeNum() > 0) {
        		String key = "feedAdvLike#" + id;
        		logger.info("初始化点赞数, key={}, likeNum={}", key, feedInner.getLikeNum());
        		CacheUtil.setToRedis(key, Constants.LONGEST_CACHE_TIME, String.valueOf(feedInner.getLikeNum()));
        	}
        }
        else if (ShowType.FULL_SCREEN.getType().equals(showType) || ShowType.FULL_SCREEN_RIDE.getType().equals(showType)
        		|| ShowType.FULL_SCREEN_MOBIKE.getType().equals(showType)) { // 浮屏广告--> 包括首页、乘车页、共享单车页
            AdFullInnerContent fullInner = new AdFullInnerContent();
            fullInner.setAndParseJson(content);
            innerContent = fullInner;
        }
        else if (ShowType.PUSH_NOTICE.getType().equals(showType)) {
            AdPushInnerContent pushInner = new AdPushInnerContent();
            pushInner.setAndParseJson(content);
            innerContent = pushInner;
        } 
        else if (ShowType.OPEN_SCREEN.getType().equals(showType)) {
            AdFullInnerContent fullInner = new AdFullInnerContent();
            fullInner.setAndParseJson(content);
            innerContent = fullInner;
        }
        else if (ShowType.LINE_DETAIL.getType().equals(showType)
        		||"07".equals(showType)||"08".equals(showType)
        		|| "12".equals(showType)) {		// 详情页广告，借用这个结构的还包括--->详情页刷新位广告，活动页广告，乘车页广告，下车提醒音频广告
            AdLineDetailInnerContent lineDetailInner = new AdLineDetailInnerContent();
            lineDetailInner.setAndParseJson(content);
            innerContent = lineDetailInner;
        }
        else if (ShowType.LINEDETAIL_REFRESH_ADV.getType().equals(showType) || ShowType.H5_LINEBANNER_ADV.getType().equals(showType)) {
        	AdLineRefreshInnerContent lineReInner = new AdLineRefreshInnerContent(); // 刷新位广告， 至简结构广告（仅图片和h5跳转）
        	lineReInner.setAndParseJson(content);
        	innerContent = lineReInner;
        } else if(ShowType.LINEDETAIL_REFRESH_OPEN_ADV.getType().equals(showType)) {
        	AdLineRefreshOpenInnerContent lineReOpenInner = new AdLineRefreshOpenInnerContent();
        	lineReOpenInner.setAndParseJson(content);
        	innerContent = lineReOpenInner;
        } else if(ShowType.LINE_FEED_ADV.getType().equals(showType)) {
            AdLineFeedInnerContent adLineFeedInnerContent = new AdLineFeedInnerContent();
            adLineFeedInnerContent.setAndParseJson(content);
            innerContent = adLineFeedInnerContent;
        } else if(ShowType.LINE_RIGHT_ADV.getType().equals(showType)) {      // 右上角广告
            AdLineRightInnerContent rightInner = new AdLineRightInnerContent();
            rightInner.setAndParseJson(content);
            innerContent = rightInner;
        }
        else {
            logger.error("[Unsupport_ShowType] 无法识别的showType: {}", showType);
        }
        
        return innerContent;
    }

    
    public AdInnerContent getAdInnerContent(){
    	return innerContent;
    }
    
    private void buildIdentity() {
        if (identity != null) {
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("ADV[id=").append(id)
            .append("#showType=").append(showType)
            .append("#title=").append((title != null && title.length() > 10) ? title.substring(0, 10) : title)
            .append("]");
        
        identity = sb.toString();
    }
    
    public String getLogKey() {
        if (this.identity == null) {
            buildIdentity();
        }
        
        return identity;
    }
    
    /**
     * 由于数据库之中只保存了图片的名字，没有保存图片的完整路径；
     * 该方法将会补全所有的这些图片路径。
     */
    public void completePicUrl() {
        AdInnerContent inner = getInnerContent(); // 将广告数据库中content字段，转换成对应类型的 AdInnerContent
        if (inner != null) {
            int isbackUp = inner.getIsBackup();
            logger.info("traceInfo set to redis ********, id={}, title={}, isbackup={}", this.id, this.title, isbackUp);
            CacheUtil.setToAtrace("AD_PROPERTY_TITLE_" + this.id, title);
            CacheUtil.setToAtrace("AD_PROPERTY_BACKUP_" + this.id, isbackUp + "");
            
            inner.completePicUrl();	 // 将AdInnerContent中的url完整化
        }
    }
    
    public String getEncodedLink() {
        if (encodedLink == null) {
            encodedLink = AdvUtil.encodeUrl(link);
        }
        return encodedLink;
    }
    
    public String getLink() {
        return link;
    }
    
    public void setLink(String link) {
        this.link = link;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AdContent adContent = (AdContent) o;

        if (id != adContent.id) return false;
        if (openType != adContent.openType) return false;
        if (status != adContent.status) return false;
        if (targetType != adContent.targetType) return false;
        if (showType != null ? !showType.equals(adContent.showType) : adContent.showType != null) return false;
        if (title != null ? !title.equals(adContent.title) : adContent.title != null) return false;
        if (link != null ? !link.equals(adContent.link) : adContent.link != null) return false;
        if (content != null ? !content.equals(adContent.content) : adContent.content != null) return false;
        if (createTime != null ? !createTime.equals(adContent.createTime) : adContent.createTime != null) return false;
        if (modifyTime != null ? !modifyTime.equals(adContent.modifyTime) : adContent.modifyTime != null) return false;
        if (innerContent != null ? !innerContent.equals(adContent.innerContent) : adContent.innerContent != null)
            return false;
        return !(encodedLink != null ? !encodedLink.equals(adContent.encodedLink) : adContent.encodedLink != null);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (showType != null ? showType.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (link != null ? link.hashCode() : 0);
        result = 31 * result + openType;
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + status;
        result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
        result = 31 * result + (modifyTime != null ? modifyTime.hashCode() : 0);
        result = 31 * result + targetType;
        result = 31 * result + (innerContent != null ? innerContent.hashCode() : 0);
        result = 31 * result + (encodedLink != null ? encodedLink.hashCode() : 0);
        return result;
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getShowType() {
		return showType;
	}

	public void setShowType(String showType) {
		this.showType = showType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getOpenType() {
		return openType;
	}

	public void setOpenType(int openType) {
		this.openType = openType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	public int getTargetType() {
		return targetType;
	}

	public void setTargetType(int targetType) {
		this.targetType = targetType;
	}

	public String getUnfoldMonitorLink() {
		return unfoldMonitorLink;
	}

	public void setUnfoldMonitorLink(String unfoldMonitorLink) {
		this.unfoldMonitorLink = unfoldMonitorLink;
	}

	public String getClickMonitorLink() {
		return clickMonitorLink;
	}

	public void setClickMonitorLink(String clickMonitorLink) {
		this.clickMonitorLink = clickMonitorLink;
	}

	public int getMonitorType() {
		return monitorType;
	}

	public void setMonitorType(int monitorType) {
		this.monitorType = monitorType;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public AdInnerContent getInnerPicContent() {
		return innerPicContent;
	}

	public void setInnerPicContent(AdInnerContent innerPicContent) {
		this.innerPicContent = innerPicContent;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public void setInnerContent(AdInnerContent innerContent) {
		this.innerContent = innerContent;
	}

	public void setEncodedLink(String encodedLink) {
		this.encodedLink = encodedLink;
	}

	public int getLink_extra() {
		return link_extra;
	}

	public void setLink_extra(int link_extra) {
		this.link_extra = link_extra;
	}


	@Override
	public String toString() {
		return "AdContent [id=" + id + ", showType=" + showType + ", title=" + title + ", link=" + link + ", openType="
				+ openType + ", content=" + content + ", status=" + status + ", createTime=" + createTime
				+ ", modifyTime=" + modifyTime + ", targetType=" + targetType + ", unfoldMonitorLink="
				+ unfoldMonitorLink + ", clickMonitorLink=" + clickMonitorLink + ", monitorType=" + monitorType
				+ ", priority=" + priority + ", link_extra=" + link_extra + ", innerContent=" + innerContent
				+ ", innerPicContent=" + innerPicContent + ", encodedLink=" + encodedLink + ", identity=" + identity
				+ "]";
	}


	public int getIs_fullScreen() {
		return is_fullScreen;
	}


	public void setIs_fullScreen(int is_fullScreen) {
		this.is_fullScreen = is_fullScreen;
	}


    /**
     * @return the projectId
     */
    public String getProjectId() {
        return projectId;
    }


    /**
     * @param projectId the projectId to set
     */
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
    
    
    
}
