package com.bus.chelaile.model.ads;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.util.New;


public class TaskModel {

    private Long timeout;
    private String apiName;
    private int priority;
    
    /**
     * @return the timeout
     */
    public Long getTimeout() {
        return timeout;
    }
    /**
     * @param timeout the timeout to set
     */
    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }
    /**
     * @return the apiName
     */
    public String getApiName() {
        return apiName;
    }
    /**
     * @param apiName the apiName to set
     */
    public void setApiName(String apiName) {
        this.apiName = apiName;
    }
    /**
     * @return the priority
     */
    public int getPriority() {
        return priority;
    }
    /**
     * @param priority the priority to set
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    public static void main(String[] args) {
        List<TaskModel> tasks = New.arrayList();
        
        TaskModel t = new TaskModel();
        t.setApiName("toutiao_sdk");
        t.setTimeout(1500L);
        t.setPriority(1);
        
        TaskModel t0 = new TaskModel();
        t0.setApiName("baidu_sdk");
        t0.setTimeout(1500L);
        t0.setPriority(2);
        
        tasks.add(t);
        tasks.add(t0);
        
        System.out.println(JSONObject.toJSONString(tasks));
        System.out.println(tasks);
    }
}
