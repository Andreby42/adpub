package com.bus.chelaile.model.ads;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.bus.chelaile.model.ads.entity.AdEntity;
import com.bus.chelaile.model.ads.entity.TasksGroup;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.util.JsonBinder;
import com.bus.chelaile.util.New;

/**
 * 换乘，更多车辆，站点所有车辆
 * @author 41945
 *
 */
public class AdCommonContent extends AdInnerContent {
    private String pic; // 广告图片的URL

    private int apiType;
    private int provider_id; // 广告提供商， 0 自采买， 2 广点通
    private String slogan;
    private String feedAdTitle;
    private int imgsType; // 图片样式：0：单图小图， 1 宽图大图

    private int adWeight; // 权重
    private long autoInterval; // 自动刷新时间
    private long mixInterval; // 最小展示时间
    private int backup; // 是否是备选方案
    private int clickDown; // 点击后排序到最后

    private int isSkip;
    private int isDisplay;
    private int duration;
    private String iosURL;
    private String androidURL;

    //  private String tasksStr; // tasks列表
    private List<TaskModel> tasksJ;
    private List<Long> timeouts; // 超时时间段设置

    private TasksGroup tasksGroup;

    protected static final Logger logger = LoggerFactory.getLogger(AdCommonContent.class);

    @Override
    protected void parseJson(String jsonr) {
        AdCommonContent ad = null;
        ad = JSON.parseObject(jsonr, AdCommonContent.class);
        if (ad != null) {
            this.isSkip = ad.isSkip;
            this.isDisplay = ad.isDisplay;
            this.duration = ad.duration;
            this.iosURL = ad.iosURL;
            this.androidURL = ad.androidURL;
            this.slogan = ad.slogan;

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
            this.clickDown = ad.clickDown;

            Map<String, String> map = New.hashMap();
            
            this.setTasksJ(ad.getTasksJ());
            List<List<String>> tasksG = New.arrayList();
            if (this.getTasksJ() != null && this.getTasksJ().size() > 0) {
                Collections.sort(tasksJ, TaskModel_COMPARATOR);
                Set<Integer> prioritys = New.hashSet();
                for (TaskModel t : getTasksJ()) {

                    map.put(t.getApiName(), t.getDisplayType() + "");

                    if (!prioritys.contains(t.getPriority())) {
                        List<String> ts = New.arrayList();
                        ts.add(t.getApiName());
                        tasksG.add(ts);
                        prioritys.add(t.getPriority());
                    } else {
                        tasksG.get(tasksG.size() - 1).add(t.getApiName());
                    }
                }
            }
            if (tasksG != null && tasksG.size() > 0 && ad.timeouts != null) {
                TasksGroup tasksGroups = new TasksGroup();
                tasksGroups.setTasks(tasksG);
                tasksGroups.setTimeouts(ad.timeouts);
                tasksGroups.setMap(map);
                tasksGroups.setClosePic(ad.getClosePic());
                this.tasksGroup = tasksGroups;
            } else if (provider_id < 2) { // 如果tasks为空，设置默认的值，既车来了api
                this.tasksGroup = createOwnAdTask(ad);
            }
            try {
                logger.info("json={}", JsonBinder.toJson(this.tasksGroup, JsonBinder.always));
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
            setCommentContext(ad, this.pic);
        }
    }

    @Override
    public int getIsBackup() {
        return this.backup;
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
    public void completePicUrl() {
        this.pic = getFullPicUrl(pic);
        this.setIosURL(getFullPicUrl(getIosURL()));
        this.setAndroidURL(getFullPicUrl(getAndroidURL()));
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

    /**
     * @return the clickDown
     */
    public int getClickDown() {
        return clickDown;
    }

    /**
     * @param clickDown the clickDown to set
     */
    public void setClickDown(int clickDown) {
        this.clickDown = clickDown;
    }

    /**
     * @return the tasksGroup
     */
    public TasksGroup getTasksGroup() {
        return tasksGroup;
    }

    /**
     * @param tasksGroup the tasksGroup to set
     */
    public void setTasksGroup(TasksGroup tasksGroup) {
        this.tasksGroup = tasksGroup;
    }

    /**
     * @return the timeouts
     */
    public List<Long> getTimeouts() {
        return timeouts;
    }

    /**
     * @param timeouts the timeouts to set
     */
    public void setTimeouts(List<Long> timeouts) {
        this.timeouts = timeouts;
    }

    /**
     * @return the tasksJ
     */
    public List<TaskModel> getTasksJ() {
        return tasksJ;
    }

    /**
     * @param tasksJ the tasksJ to set
     */
    public void setTasksJ(List<TaskModel> tasksJ) {
        this.tasksJ = tasksJ;
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

}
