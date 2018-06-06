package com.bus.chelaile.model.ads;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.shade.com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.model.ads.entity.AdEntity;
import com.bus.chelaile.model.ads.entity.TasksGroup;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.util.New;

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
    private int clickDown; // 点击后排序到最后

    //    private String tasksStr; // tasks列表
    private List<TaskModel> tasksJ;
    private List<Long> timeouts; // 超时时间段设置

    private TasksGroup tasksGroup;

    @Autowired
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
            this.clickDown = ad.clickDown;
            this.setTasksJ(ad.getTasksJ());
            //            this.timeouts = ad.timeouts;

            List<List<String>> tasksG = New.arrayList();
            if (this.getTasksJ() != null && this.getTasksJ().size() > 0) {
                Collections.sort(tasksJ, TaskModel_COMPARATOR);
//                getTasksJ().sort((final TaskModel t1, final TaskModel t2) -> (t1.getPriority() - t2.getPriority()));
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
            }
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

    public static void main(String[] args) {
        List<TaskModel> tasksJ1 = New.arrayList();
        TaskModel t1 = new TaskModel();
        t1.setApiName("a");
        t1.setPriority(1);
        TaskModel t2 = new TaskModel();
        t2.setApiName("a2");
        t2.setPriority(2);
        tasksJ1.add(t2);
        tasksJ1.add(t1);

        System.out.println(JSONObject.toJSONString(tasksJ1));
        Collections.sort(tasksJ1, TaskModel_COMPARATOR);
        System.out.println(JSONObject.toJSONString(tasksJ1));

        AdFullInnerContent adPush = new AdFullInnerContent();
        adPush.setAndParseJson("{\"timeouts\":[500,1500],\"tasksJ\":[{\"apiName\":\"sdk_toutiao\",\"priority\":1},{\"apiName\":\"sdk_baidu\",\"priority\":1},{\"apiName\":\"sdk_gdt\",\"priority\":2}]}");
        System.out.println("pic: " + adPush.pic);
        System.out.println("JsonR: " + adPush.jsonContent);
        System.out.println(JSONObject.toJSONString(adPush));
        System.out.println(adPush.getTasksGroup().getTimeouts().toString());
        System.out.println(adPush.getTasksGroup().getTasks().toString());
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
    
    private static final Comparator<TaskModel> TaskModel_COMPARATOR = new Comparator<TaskModel>() {
        @Override
        public int compare(TaskModel o1, TaskModel o2) {
            if (o1 == null)
                return -1;
            if (o2 == null)
                return 1;
            return o1.getPriority() - o2.getPriority();
        }
    };

}
