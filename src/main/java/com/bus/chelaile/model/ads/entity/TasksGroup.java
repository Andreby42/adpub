package com.bus.chelaile.model.ads.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.aliyun.openservices.shade.com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.util.New;

public class TasksGroup {

    private List<List<String>> tasks;
    private List<Long> timeouts;
    // 参数名字,参数值
    private Map<String,String> map;
    
    private String closePic;
    
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


    public TasksGroup(List<List<String>> tasks, List<Long> timeouts,Map<String,String> map) {
        super();
        this.tasks = tasks;
        this.timeouts = timeouts;
        this.map = map;
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

	public Map<String, String> getMap() {
		return map;
	}

	public void setMap(Map<String, String> map) {
		this.map = map;
	}

    public String getClosePic() {
        return closePic;
    }

    public void setClosePic(String closePic) {
        this.closePic = closePic;
    }



    


}
