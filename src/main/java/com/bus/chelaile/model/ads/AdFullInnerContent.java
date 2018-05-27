package com.bus.chelaile.model.ads;

import com.alibaba.fastjson.JSON;
import com.bus.chelaile.model.ads.entity.AdEntity;
import com.bus.chelaile.mvc.AdvParam;

/**
 * 推送的广告的内部的内容， 就是数据之中content的结构化表示。
 * @author liujh
 *
 */
public class AdFullInnerContent extends AdInnerContent {
    private String pic; //广告图片的URL
    private int isSkip;
    private int isDisplay;
    private int duration;
    private String iosURL;
    private String androidURL;

    private String tag; //话题标签名
    private String tagId; //话题标签id,数据库存储的是String类型。
    private String feedId; //话题详情页id

    private int apiType;
    private int provider_id; // 广告提供商， 0：自采买广告；8：阅盟；9：Ecoook；10：科大讯飞【其他说明：3 inmobi】
    private int backup;
    private int timeout; // 超时
    private int AdWeight; // 权重

    @Override
    protected void parseJson(String jsonr) {
        AdFullInnerContent ad = null;
        ad = JSON.parseObject(jsonr, AdFullInnerContent.class);
        if (ad != null) {
            this.pic = ad.pic;
            this.isSkip = ad.isSkip;
            this.isDisplay = ad.isDisplay;
            this.duration = ad.duration;
            this.iosURL = ad.iosURL;
            this.androidURL = ad.androidURL;

            this.tag = ad.tag;
            this.tagId = ad.tagId;
            this.feedId = ad.feedId;
            this.provider_id = ad.provider_id;
            this.apiType = ad.apiType;
            this.backup = ad.backup;
            this.timeout = ad.timeout;
            this.AdWeight = ad.AdWeight;
        }
    }

    @Override
    public String extractFullPicUrl(String s) {
        if (pic != null && !pic.equals("")) {
            return getFullPicUrl(getPic());
        }
        if (s.equalsIgnoreCase("ios")) {
            return getFullPicUrl(getIosURL());
        } else {
            return getFullPicUrl(getAndroidURL());
        }
    }

    @Override
    public String extractAudiosUrl(String s, int type) {
        return null;
    }

    public static void main(String[] args) {
        AdFullInnerContent adPush = new AdFullInnerContent();
        adPush.setAndParseJson("{\"pic\":\"http://cdn.www.chelaile.net.cn/img/subway/line10_pic.png\"}");
        System.out.println("pic: " + adPush.pic);
        System.out.println("JsonR: " + adPush.jsonContent);
    }

    @Override
    public void fillAdEntity(AdEntity adEntity, AdvParam param, int stindex) {
        if (adEntity == null) {
            return;
        }
    }

    public void completePicUrl() {
        this.pic = getFullPicUrl(pic);
        this.iosURL = getFullPicUrl(iosURL);
        this.androidURL = getFullPicUrl(androidURL);
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getIosURL() {
        return iosURL;
    }

    public void setIosURL(String iosURL) {
        this.iosURL = iosURL;
    }

    public String getAndroidURL() {
        return androidURL;
    }

    public void setAndroidURL(String androidURL) {
        this.androidURL = androidURL;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getFeedId() {
        return feedId;
    }

    public void setFeedId(String feedId) {
        this.feedId = feedId;
    }

    public int getProvider_id() {
        return provider_id;
    }

    public void setProvider_id(int provider_id) {
        this.provider_id = provider_id;
    }

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
     * @return the timeout
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * @param timeout the timeout to set
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * @return the adWeight
     */
    public int getAdWeight() {
        return AdWeight;
    }

    /**
     * @param adWeight the adWeight to set
     */
    public void setAdWeight(int adWeight) {
        AdWeight = adWeight;
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
