package com.bus.chelaile.model.ads;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.model.TypeNumber;
import com.bus.chelaile.model.ads.entity.AdEntity;
import com.bus.chelaile.model.ads.entity.TasksGroup;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.util.GpsUtils;
import com.bus.chelaile.util.New;
import com.bus.chelaile.util.config.PropertiesUtils;

/**
 * 双栏广告的内部内容， 需要解析之后在转储到返回给调用者的对象之中
 * @author liujh
 *
 */
public class AdDoubleInnerContent extends AdInnerContent {

    private String brandIcon;
    private String brandName;
    private String promoteTitle;
    private int showDistance; //是否显示距离，0不显示1显示
    private String barColor;
    private String head;
    private String subhead;
    private int buttonType; //按钮类型，0普通按钮，1icon
    private String buttonIcon;
    private String buttonTitle;
    private String buttonColor;

    private String iosURL;
    private String androidURL;

    //新增加的属性
    private double lng = -1.0;
    private double lat = -1.0;

    private String tag; //话题标签名
    private String tagId; //话题标签id
    private String feedId; //话题详情页id

    private String desc; //

    /**
    * 站级别位置，双栏广告的显示位置：第n位， 0表示第一条线前面（首位），
    * 1表示第一条线后面，2表示第二条线后面，等，而-1表示最后一条线后面（末位）。
    */
    // 2018-04-28更新， 只支持0 OR 1
    private int position = Constants.NULL_POSITION;

    private int apiType;
    private int provider_id; // 广告提供商， 0 自采买， 2 广点通
    private long autoInterval; // 自动刷新时间
    private long mixInterval; // 最小展示时间
    private int adWeight; //权重
    private int backup; // 是否是备选方案
    private int clickDown; // 点击后排序到最后

    //  private String tasksStr; // tasks列表
    private List<TaskModel> tasksJ;
    private List<Long> timeouts; // 超时时间段设置

    private TasksGroup tasksGroup;

    @Override
    public void parseJson(String jsonr) {
        AdDoubleInnerContent ad = null;
        ad = JSON.parseObject(jsonr, AdDoubleInnerContent.class);
        if (ad != null) {
            this.brandIcon = ad.brandIcon;
            this.brandName = ad.brandName;
            this.promoteTitle = ad.promoteTitle;
            this.showDistance = ad.showDistance;
            this.barColor = ad.barColor;
            this.head = ad.head;
            this.subhead = ad.subhead;
            this.buttonType = ad.buttonType;
            this.buttonIcon = ad.buttonIcon;
            this.buttonTitle = ad.buttonTitle;
            this.buttonColor = ad.buttonColor;

            this.lng = ad.lng;
            this.lat = ad.lat;
            this.position = ad.position;

            this.tag = ad.tag;
            this.tagId = ad.tagId;
            this.feedId = ad.feedId;

            this.provider_id = ad.provider_id;
            this.apiType = ad.apiType;
            this.desc = ad.desc;

            this.autoInterval = ad.autoInterval * 1000;
            this.mixInterval = ad.mixInterval * 1000;
            this.backup = ad.backup;
            this.adWeight = ad.adWeight;
            this.clickDown = ad.clickDown;

            this.setTasksJ(ad.getTasksJ());
            List<List<String>> tasksG = New.arrayList();
            if (this.getTasksJ() != null && this.getTasksJ().size() > 0) {
                Collections.sort(tasksJ, TaskModel_COMPARATOR);
                Set<Integer> prioritys = New.hashSet();
                for (TaskModel t : getTasksJ()) {
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
                this.tasksGroup = tasksGroups;
            } else if (provider_id < 2) {    // 如果tasks为空，设置默认的值，既车来了api
                this.tasksGroup = createOwnAdTask();
            }
        }
    }
    
    @Override
    public int getIsBackup() {
        return this.backup;
    }

    @Override
    public String extractFullPicUrl(String s) {
        return null;
    }

    @Override
    public String extractAudiosUrl(String s, int type) {
        return null;
    }

    @Override
    public void fillAdEntity(AdEntity adEntity, AdvParam param, int stindex) {
        if (adEntity == null) {
            return;
        }

        adEntity.setBrandIcon(this.brandIcon);
        adEntity.setBrandName(this.brandName);
        adEntity.setPromoteTitle(this.promoteTitle);

        if (this.showDistance == 1 && this.lng > 0 && this.lat > 0 && param != null) {
            double dist = GpsUtils.geo_distance(this.lng, this.lat, param.getLng(), param.getLat());
            adEntity.setDistance((int) (dist * 1000));
        } else {
            adEntity.setDistance(-1);
        }

        adEntity.setBarColor(this.barColor);
        adEntity.setHead(this.head);
        adEntity.setSubhead(this.subhead);
        adEntity.setButtonType(this.buttonType);
        adEntity.setButtonIcon(this.buttonIcon);
        adEntity.setButtonTitle(nullToEmpty(this.buttonTitle));
        adEntity.setButtonColor(nullToEmpty(this.buttonColor));
        adEntity.setFeedId(this.getFeedId());

        adEntity.setMixInterval(this.getMixInterval());
        adEntity.setAdWeight(this.adWeight);
        adEntity.setAutoInterval(this.autoInterval);

        if (param.getType() == TypeNumber.ONE.getType()) { // type=1：线路规划页广告
            adEntity.setDesc(this.desc);
        }
        if (this.tag != null && this.tagId != null) {
            adEntity.setTag(new Tag(this.tag, this.getTagId()));
        }

        if (param.getlSize() == -1) { // 这个参数控制版本，Constants.PLATFORM_LOG_ANDROID_0326 之前的老版本
            if (position != Constants.NULL_POSITION) {
                adEntity.setSindex(position == -1 ? (stindex + 1) : (position == 0 ? 0 : 1));
            } else {
                adEntity.setSindex(getStationLevelDefaultPosition());
            }
        } else {
            adEntity.setSindex(position == 0 ? 0 : 1); // 仅限 0 和 1
        }
        adEntity.setLindex(0);
    }

    private String nullToEmpty(String str) {
        return str == null ? "" : str;
    }

    public static int getStationLevelDefaultPosition() {
        return Integer.parseInt(PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "adv.station.level.default.pos", "1"));
        //return PropertiesReaderWrapper.readInt("adv.station.level.default.pos", 1);
    }

    public void completePicUrl() {
        this.brandIcon = getFullPicUrl(brandIcon);
        this.buttonIcon = getFullPicUrl(buttonIcon);
    }

    public static void main(String[] args) {
        String s = "{\"provider_id\":2,\"position\":1,\"promoteTitle\":\"活动\",\"showDistance\":0,\"subhead\":\"上班不迟到，放心睡懒觉\"}";

        AdDoubleInnerContent ad = new AdDoubleInnerContent();
        ad.parseJson(s);

        System.out.println(JSONObject.toJSONString(ad));
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

    public int getShowDistance() {
        return showDistance;
    }

    public void setShowDistance(int showDistance) {
        this.showDistance = showDistance;
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

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
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

    public int getProvider_id() {
        return provider_id;
    }

    public void setProvider_id(int provider_id) {
        this.provider_id = provider_id;
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
}
