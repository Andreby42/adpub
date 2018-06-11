package com.bus.chelaile.model.ads.entity;

import java.util.ArrayList;
import java.util.List;

import com.aliyun.openservices.shade.com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.util.New;

public class TasksGroup {

    private List<List<String>> tasks;
    private List<Long> timeouts;
    
    public static void main(String[] args) {
        
        TasksGroup tgs = new TasksGroup();
        List<String> task1 = New.arrayList();
        task1.add("api_voicead");
        task1.add("sdk_toutiao");

        List<String> task2 = New.arrayList();
        task2.add("sdk_baidu");

        List<List<String>> ts = new ArrayList<List<String>>();
        ts.add(task1);
        ts.add(task2);

        List<Long> times = New.arrayList();
        times.add(200L);
        times.add(1500L);

        tgs.setTasks(ts);
        tgs.setTimeouts(times);
        
        System.out.println(JSONObject.toJSON(tgs));
        System.out.println(tgs.getTasks().toString());
        System.out.println(tgs.getTimeouts().toString());
        
    }

    public TasksGroup() {
        super();
    }


    public TasksGroup(List<List<String>> tasks, List<Long> timeouts) {
        super();
        this.tasks = tasks;
        this.timeouts = timeouts;
    }

    /**
     * @return the tasks
     */
    public List<List<String>> getTasks() {
        return tasks;
    }




    /**
     * @param tasks the tasks to set
     */
    public void setTasks(List<List<String>> tasks) {
        this.tasks = tasks;
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


}
