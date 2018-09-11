package com.bus.chelaile.model.ads.entity;

public class TaskEntity {

    private TasksGroup taskGroups;
    private String traceid;
    private String adDataString;
    private int jsid;
    
    
    /**
     * @return the traceid
     */
    public String getTraceid() {
        return traceid;
    }
    /**
     * @param traceid the traceid to set
     */
    public void setTraceid(String traceid) {
        this.traceid = traceid;
    }
    /**
     * @return the taskGroups
     */
    public TasksGroup getTaskGroups() {
        return taskGroups;
    }
    /**
     * @param taskGroups the taskGroups to set
     */
    public void setTaskGroups(TasksGroup taskGroups) {
        this.taskGroups = taskGroups;
    }
    public String getAdDataString() {
        return adDataString;
    }
    public void setAdDataString(String adDataString) {
        this.adDataString = adDataString;
    }
    public int getJsid() {
        return jsid;
    }
    public void setJsid(int jsid) {
        this.jsid = jsid;
    }
    
}
