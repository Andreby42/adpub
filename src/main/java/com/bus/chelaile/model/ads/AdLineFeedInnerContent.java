package com.bus.chelaile.model.ads;

import com.alibaba.fastjson.JSON;
import com.bus.chelaile.model.ads.entity.AdEntity;
import com.bus.chelaile.mvc.AdvParam;

/**
 * 广告的内部的内容， 就是数据之中content的结构化表示。
 * 
 * @author linzi
 * 
 */
public class AdLineFeedInnerContent extends AdInnerContent {
    private String pic; // 广告图片的URL
    
    private int adWeight; // 权重
    private long autoInterval; // 自动刷新时间
    private long mixInterval; // 最小展示时间
    private int backup; // 是否是备选方案
    
    private int apiType;
    private int provider_id; // 广告提供商， 0 自采买， 2 广点通
    private String slogan;
    private String feedAdTitle;
    private int imgsType; // 图片样式：0：单图小图， 1 宽图大图

    
    @Override
    protected void parseJson(String jsonr) {
        AdLineFeedInnerContent ad = null;
        ad = JSON.parseObject(jsonr, AdLineFeedInnerContent.class);
        if (ad != null) {
            this.adWeight = ad.adWeight;
            this.autoInterval = ad.autoInterval * 1000;
            this.mixInterval = ad.mixInterval * 1000;
            this.backup = ad.backup;
            this.apiType = ad.apiType;
            this.setProvider_id(ad.getProvider_id());
            this.setSlogan(ad.getSlogan());
            this.setFeedAdTitle(ad.getFeedAdTitle());
            this.setImgsType(ad.getImgsType());
            this.pic = ad.pic;
        }
    }

    @Override
    public String extractFullPicUrl(String s) {
        return null;
    }

    @Override
    public String extractAudiosUrl(String s, int type) {
        return null;
    }

    public static void main(String[] args) {
        AdLineFeedInnerContent ad = new AdLineFeedInnerContent();
        ad.setAndParseJson("{\"provider_id\":2,\"adWeight\":10,\"autoInterval\":15,\"mixInterval\":3}");
        System.out.println(ad.getProvider_id() + "," + ad.getAdWeight() + "," + ad.getAutoInterval() + "," + ad.getMixInterval());
    }

    @Override
    public void fillAdEntity(AdEntity adEntity, AdvParam param, int stindex) {
        if (adEntity == null) {
            return;
        }
    }

    public long getAutoInterval() {
        return autoInterval;
    }

    public void setAutoInterval(long autoInterval) {
        this.autoInterval = autoInterval;
    }

    public long getMixInterval() {
        return mixInterval;
    }

    public void setMixInterval(long mixInterval) {
        this.mixInterval = mixInterval;
    }

    public int getAdWeight() {
        return adWeight;
    }

    public void setAdWeight(int adWeight) {
        this.adWeight = adWeight;
    }

    @Override
    public void completePicUrl() {}


    /**
     * @return the backup
     */
    public int getBackup() {
        return backup;
    }

    /**
     * @param backup the backup to set
     */
    public void setBackup(int backup) {
        this.backup = backup;
    }

    /**
     * @return the pic
     */
    public String getPic() {
        return pic;
    }

    /**
     * @param pic the pic to set
     */
    public void setPic(String pic) {
        this.pic = pic;
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
     * @return the feedAdTitle
     */
    public String getFeedAdTitle() {
        return feedAdTitle;
    }

    /**
     * @param feedAdTitle the feedAdTitle to set
     */
    public void setFeedAdTitle(String feedAdTitle) {
        this.feedAdTitle = feedAdTitle;
    }

    /**
     * @return the slogan
     */
    public String getSlogan() {
        return slogan;
    }

    /**
     * @param slogan the slogan to set
     */
    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    /**
     * @return the provider_id
     */
    public int getProvider_id() {
        return provider_id;
    }

    /**
     * @param provider_id the provider_id to set
     */
    public void setProvider_id(int provider_id) {
        this.provider_id = provider_id;
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

}
