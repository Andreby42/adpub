package com.bus.chelaile.model.ads;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.bus.chelaile.model.ads.entity.AdEntity;
import com.bus.chelaile.model.ads.entity.TasksGroup;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.util.New;

/**
 * 广告的内部的内容， 就是数据之中content的结构化表示。
 * 
 * @author liujh
 * 
 */
public class AdLineRightInnerContent extends AdInnerContent {
    private String pic; // 广告图片的URL
    private int adMode; // 广告显示控制模式（不同的二进制位表示不同区域的图片显示，详见LineDetailAdMode
    private int provider_id;
    private int backup; // 是否是备选方案
    private long autoInterval; // 自动刷新时间
    private long mixInterval; // 最小展示时间
    //    private String tag;	//话题标签名
    //    private String tagId;	//话题标签id
    //    private String feedId; //话题详情页id

    private List<TaskModel> tasksJ;
    private List<Long> timeouts; // 超时时间段设置

    private TasksGroup tasksGroup;

    @Override
    protected void parseJson(String jsonr) {
        AdLineRightInnerContent ad = null;
        ad = JSON.parseObject(jsonr, AdLineRightInnerContent.class);
        if (ad != null) {
            this.pic = ad.pic;
            this.provider_id = ad.provider_id;
            this.backup = ad.backup;
            this.autoInterval = ad.autoInterval;
            this.mixInterval = ad.mixInterval;
            if (ad.pic != null && ad.pic.contains("#") && ad.pic.contains(",")) {
                this.pic = ad.pic.split("#")[0];
                //				this.setWidth(Integer.parseInt(ad.pic.split("#")[1].split(",")[0]));
                //				this.setHeight(Integer.parseInt(ad.pic.split("#")[1].split(",")[1]));
            }
            this.setTasksJ(ad.getTasksJ());
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
            } else { // 如果tasks为空，设置默认的值，既车来了api
                TasksGroup tasksGroups = new TasksGroup();
                List<String> ts = New.arrayList();
                ts.add("api_chelaile");
                List<List<String>> tasks = New.arrayList();
                tasks.add(ts);
                List<Long> times = New.arrayList();
                times.add(4000L);
                times.add(4000L);
                tasksGroups.setTasks(tasks);
                tasksGroups.setTimeouts(times);
                this.tasksGroup = tasksGroups;
            }
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
        //		LineRefreshInnerContent ad = new LineRefreshInnerContent();
        //		ad.setAndPaseJson("{\"feedAdTitle\":\"\",\"feedAdType\":1,\"feedId\":\"642191129076404224\",\"feedTag\":\"广告\",\"icon\":\"\",\"isSetTop\":1,\"likeNum\":0,\"pic\":\"https://image3.chelaile.net.cn/cf08752ed77849afb8f29d6b6abf2f35\",\"slogan\":\"\",\"tagId\":\"\",\"time\":0}");
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
     * @return the adMode
     */
    public int getAdMode() {
        return adMode;
    }

    /**
     * @param adMode the adMode to set
     */
    public void setAdMode(int adMode) {
        this.adMode = adMode;
    }
}
