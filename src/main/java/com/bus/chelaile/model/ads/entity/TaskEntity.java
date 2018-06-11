package com.bus.chelaile.model.ads.entity;

public class TaskEntity {

    private TasksGroup taskGroups;
    private String traceid;
    
    
    
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
    
}
