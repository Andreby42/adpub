package com.bus.chelaile.model.ads;

import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.model.ads.entity.AdEntity;
import com.bus.chelaile.model.ads.entity.TasksGroup;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.util.New;
import com.bus.chelaile.util.config.PropertiesUtils;



public abstract class AdInnerContent {
    private static final String DEFAULT_PIC_URL_PREFIX = "http://pic1.chelaile.net.cn/adv/%1$s";
    protected String jsonContent;
    
    public void setAndParseJson(String jsonr) {
        this.jsonContent = jsonr;
        parseJson(jsonr);
    }
    
    public void fillAdEntity(AdEntity adEntity, AdvParam param, int stindex) {
        // only Single & Double column will implements this methods.
        // throw new UnsupportedOperationException();
    }
    
    // 数据库中存储类似"android1353e032-43ca-4c3c-b236-74dc71787713.png","iosURL":"ios026a6280-fe4a-4820-a70e-e4648b2ba7a8.png"
    // 加前缀组成url
    public String getFullPicUrl(String picName) {
        if (StringUtils.isBlank(picName) || picName.startsWith("http://") || picName.startsWith("https://")) {
            return picName;
        }
        
        String picUrlPrefix = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(), "pic.url.prefix",DEFAULT_PIC_URL_PREFIX);
        
        return String.format(picUrlPrefix, picName);
    }
    
    public abstract void completePicUrl();
    protected abstract void parseJson(String jsonr);

    /**
     * 抽取广告内容中的全路径图片url
     * @return 完整图片url
     */
    public abstract String extractFullPicUrl(String s);
    
    public abstract String extractAudiosUrl(String s, int type);
    
    protected static final Comparator<TaskModel> TaskModel_COMPARATOR = new Comparator<TaskModel>() {
        @Override
        public int compare(TaskModel o1, TaskModel o2) {
            if (o1 == null)
                return -1;
            if (o2 == null)
                return 1;
//            return o2.getPriority() - o1.getPriority();
            return o1.getPriority() - o2.getPriority();
        }
    };
    
    public int getIsBackup() {
        return 0;
    }
    
    protected TasksGroup createOwnAdTask() {
        TasksGroup tasksGroups = new TasksGroup();
        List<String> ts = New.arrayList();
        ts.add("api_chelaile");
        List<List<String>> tasks = New.arrayList();
        tasks.add(ts);
        List<Long> times = New.arrayList();
        times.add(4000L);times.add(4000L);
        tasksGroups.setTasks(tasks);
        tasksGroups.setTimeouts(times);
        return tasksGroups;
    }
    
}
