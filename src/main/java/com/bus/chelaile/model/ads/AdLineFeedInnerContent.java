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
public class AdLineFeedInnerContent extends AdFeedInnerContent {
    private int adWeight; // 权重
    private long autoInterval; // 自动刷新时间
    private long mixInterval; // 最小展示时间
    private int provider_id; // 广告提供商

    @Override
    protected void parseJson(String jsonr) {
        AdLineFeedInnerContent ad = null;
        ad = JSON.parseObject(jsonr, AdLineFeedInnerContent.class);
        if (ad != null) {
            this.adWeight = ad.adWeight;
            this.autoInterval = ad.autoInterval * 1000;
            this.mixInterval = ad.mixInterval * 1000;
            this.provider_id = ad.provider_id;
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

    public int getProvider_id() {
        return provider_id;
    }

    public void setProvider_id(int provider_id) {
        this.provider_id = provider_id;
    }

}
