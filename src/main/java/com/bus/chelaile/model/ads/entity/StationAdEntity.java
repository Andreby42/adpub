package com.bus.chelaile.model.ads.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.model.ads.AdCard;
import com.bus.chelaile.model.ads.BannerInfo;

public class StationAdEntity extends BaseAdEntity {

    private String pic; // 图片URL
    private BannerInfo bannerInfo;
    private AdCard adCard;
    private String wxMiniProId; // 小程序appId
    private String wxMiniProPath;

//    @JSONField(serialize = false)
//    private int adWeight; // 轮播权重
    @JSONField(serialize = false)
    private int buyOut;// 买断， 0 没有买断； 1 买断。返回的entites里面如果存在这样的广告，那么他将获得最高优先级   2018-03-29
    @JSONField(serialize = false)
    private String title;
    @JSONField(serialize = false)
    private long autoInterval;
    @JSONField(serialize = false)
    private long mixInterval;

    // 构造方法
    public StationAdEntity() {
        super(ShowType.STATION_ADV.getValue());
        this.pic = EMPTY_STR;
    }

    @Override
    protected ShowType gainShowTypeEnum() {
        return ShowType.STATION_ADV;
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

    public BannerInfo getBannerInfo() {
        return bannerInfo;
    }

    public void setBannerInfo(BannerInfo bannerInfo) {
        this.bannerInfo = bannerInfo;
    }

    public AdCard getAdCard() {
        return adCard;
    }

    public void setAdCard(AdCard adCard) {
        this.adCard = adCard;
    }

//    public int getAdWeight() {
//        return adWeight;
//    }
//
//    public void setAdWeight(int adWeight) {
//        this.adWeight = adWeight;
//    }

    public int getBuyOut() {
        return buyOut;
    }

    public void setBuyOut(int buyOut) {
        this.buyOut = buyOut;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWxMiniProId() {
        return wxMiniProId;
    }

    public void setWxMiniProId(String wxMiniProId) {
        this.wxMiniProId = wxMiniProId;
    }

    public String getWxMiniProPath() {
        return wxMiniProPath;
    }

    public void setWxMiniProPath(String wxMiniProPath) {
        this.wxMiniProPath = wxMiniProPath;
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
