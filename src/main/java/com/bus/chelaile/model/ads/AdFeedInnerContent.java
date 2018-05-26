package com.bus.chelaile.model.ads;

import com.alibaba.fastjson.JSON;
import com.bus.chelaile.model.ads.entity.AdEntity;
import com.bus.chelaile.mvc.AdvParam;

/**
 * 推送的广告的内部的内容， 就是数据之中content的结构化表示。
 * 
 * @author liujh
 * 
 */
public class AdFeedInnerContent extends AdInnerContent {
    private String pic; // 广告图片的URL

    private int feedAdType; // feed流广告类型，0 话题样式， 1 透视样式， 2 文章样式， 3 图片样式

    private int width; //图片宽
    private int height; //图片高
    private String feedAdTitle;
    private String feedId;
    private String tagId;
    private String tag;

    private long time; // 这个字段已经废弃了。 改用 ‘广告生效时间’ 作为时间戳返回给客户端
    private String slogan;
    private String icon;
    private int likeNum;
    private String feedTag;
    private int isSetTop;

    // 文章样式新增字段
    private String imgs;
    private String feedAdArticleTag;

    private int imgsType; // 图片样式：0：单图小图，1：三图小图，2：透视大图样式, 3:话题样式, 4 宽图窄图(置顶用)， 5 宽图大图

    private int provider_id; // 广告提供商， 0 自采买， 2 广点通
    private int api_type; // 第三方广告类型，比如： 原生、banner等

    @Override
    protected void parseJson(String jsonr) {
        AdFeedInnerContent ad = null;
        ad = JSON.parseObject(jsonr, AdFeedInnerContent.class);
        if (ad != null) {
            try {
                this.pic = ad.pic;
                if (ad.pic != null && ad.pic.contains(";")) {

                } else if (ad.pic != null && ad.pic.contains("#") && ad.pic.contains(",")) {
                    this.pic = ad.pic.split("#")[0];
                    this.setWidth(Integer.parseInt(ad.pic.split("#")[1].split(",")[0]));
                    this.setHeight(Integer.parseInt(ad.pic.split("#")[1].split(",")[1].split(";")[0]));
                }
                this.feedAdType = ad.feedAdType;
                this.setFeedAdTitle(ad.getFeedAdTitle());
                this.feedId = ad.feedId;
                this.tagId = ad.tagId;
                this.tag = ad.tag;

                this.setTime(ad.getTime());
                this.slogan = ad.slogan;
                this.icon = ad.icon;
                this.setLikeNum(ad.getLikeNum());
                this.feedTag = ad.feedTag;
                this.isSetTop = ad.isSetTop;

                this.imgs = ad.imgs;
                this.feedAdArticleTag = ad.feedAdArticleTag;

                this.imgsType = ad.imgsType;
                this.provider_id = ad.provider_id;
                this.api_type = ad.api_type;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getFeedId() {
        return feedId;
    }

    public void setFeedId(String feedId) {
        this.feedId = feedId;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getFeedTag() {
        return feedTag;
    }

    public void setFeedTag(String feedTag) {
        this.feedTag = feedTag;
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
        AdFeedInnerContent ad = new AdFeedInnerContent();
        //		ad.setPic("https://pic1.chelaile.net.cn/adv/androidcdb2fd75-7484-4ab3-a149-c9571260b25a.jpg");
        //		ad.setLikeNum(12312);
        //		ad.setTime(System.currentTimeMillis());
        //		ad.setFeedAdTitle("苏宁");
        //		ad.setSlogan("来苏宁买东西！");
        //		ad.setIcon("https://image3.chelaile.net.cn/98949248b15141a9b5eb0759097b68eb");
        //		
        //		System.out.println(JSONObject.toJSONString(ad));
        //		
        //		ad.setAndPaseJson("{\"feedAdTitle\":\"测试\",\"feedAdType\":0,\"feedId\":\"\",\"feedTag\":\"广告\",\"icon\":\"https://image3.chelaile.net.cn/cae25767d4d24a68aad60ee6702ab045#80,80\",\"isSetTop\":0,\"likeNum\":1000,\"pic\":\"https://image3.chelaile.net.cn/d2da9b37a3734042980c217b2ac1874e#92,32\",\"slogan\":\"卡联卡联看了看\",\"tagId\":\"80\",\"time\":1514889629000}");
        //		System.out.println("pic: " + ad.pic);
        //		System.out.println("picWidth:" + ad.getWidth());
        //		System.out.println("JsonR: " + ad.jsonContent);

        ad.setAndParseJson(
                "{\"feedAdTitle\":\"\",\"feedAdType\":1,\"feedId\":\"642191129076404224\",\"feedTag\":\"广告\",\"icon\":\"\",\"isSetTop\":1,\"likeNum\":0,\"pic\":\"https://image3.chelaile.net.cn/cf08752ed77849afb8f29d6b6abf2f35\",\"slogan\":\"\",\"tagId\":\"\",\"time\":0}");
    }

    @Override
    public void fillAdEntity(AdEntity adEntity, AdvParam param, int stindex) {
        if (adEntity == null) {
            return;
        }
    }

    public void completePicUrl() {
        this.pic = getFullPicUrl(pic);
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }

    public String getFeedAdTitle() {
        return feedAdTitle;
    }

    public void setFeedAdTitle(String feedAdvTitle) {
        this.feedAdTitle = feedAdvTitle;
    }

    public int getIsSetTop() {
        return isSetTop;
    }

    public void setIsSetTop(int isSetTop) {
        this.isSetTop = isSetTop;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getFeedAdType() {
        return feedAdType;
    }

    public void setFeedAdType(int feedAdType) {
        this.feedAdType = feedAdType;
    }

    public String getImgs() {
        return imgs;
    }

    public void setImgs(String imgs) {
        this.imgs = imgs;
    }

    public String getFeedAdArticleTag() {
        return feedAdArticleTag;
    }

    public void setFeedAdArticleTag(String feedAdArticleTag) {
        this.feedAdArticleTag = feedAdArticleTag;
    }

    public int getImgsType() {
        return imgsType;
    }

    public void setImgsType(int imgsType) {
        this.imgsType = imgsType;
    }

    public int getProvider_id() {
        return provider_id;
    }

    public void setProvider_id(int provider_id) {
        this.provider_id = provider_id;
    }

    public int getApi_type() {
        return api_type;
    }

    public void setApi_type(int api_type) {
        this.api_type = api_type;
    }
}
