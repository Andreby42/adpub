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
    
    protected int displayType = 2;//    1 左⽂右图⼤，运营叫单图大图 2 左⽂右图小，运营叫单图 3 三图 4 通栏广告， 5 右侧单图（跳转信息流，没有‘广告’字样） ,2018-06-19 增加，满足三图片要求 ,2018-06-19 增加，满足三图片要求

    protected List<String> picsList;
    
    protected String closePic = "";
    protected String wx_miniPro_id;
    protected String wx_miniPro_path; // 跳转小程序， 2018-04-09出现； 2018-08-08提到通用
    
    public void setAndParseJson(String jsonr) {
        this.jsonContent = jsonr;
        parseJson(jsonr);
    }
    
    
    protected void setCommentContext(AdInnerContent ad, String pic) {
        this.displayType = ad.getDisplayType();
        this.picsList = ad.getPicsList();
        this.closePic = ad.getClosePic();
        this.setWx_miniPro_id(ad.getWx_miniPro_id());
        this.setWx_miniPro_path(ad.getWx_miniPro_path());
        if (this.getWx_miniPro_id() != null)
            this.setWx_miniPro_id(this.getWx_miniPro_id().trim());
        if (this.getWx_miniPro_path() != null)
            this.setWx_miniPro_path(this.getWx_miniPro_path().trim());
        if (picsList == null || picsList.size() == 0) {
            if (pic != null && StringUtils.isNoneBlank(pic)) {
                picsList = New.arrayList();
                picsList.add(pic);
            }

        }

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
    
    protected TasksGroup createOwnAdTask(AdInnerContent ad) {
        TasksGroup tasksGroups = new TasksGroup();
        List<String> ts = New.arrayList();
        ts.add("api_chelaile");
        List<List<String>> tasks = New.arrayList();
        tasks.add(ts);
        List<Long> times = New.arrayList();
        times.add(4000L);times.add(4000L);
        tasksGroups.setTasks(tasks);
        tasksGroups.setTimeouts(times);
        tasksGroups.setClosePic(ad.getClosePic());
        return tasksGroups;
    }
    
    public int getDisplayType() {
		return displayType;
	}

	public void setDisplayType(int displayType) {
		this.displayType = displayType;
	}

	public List<String> getPicsList() {
		return picsList;
	}

	public void setPicsList(List<String> picsList) {
		this.picsList = picsList;
	}


    public String getClosePic() {
        return closePic;
    }


    public void setClosePic(String closePic) {
        this.closePic = closePic;
    }


    public String getWx_miniPro_id() {
        return wx_miniPro_id;
    }


    public void setWx_miniPro_id(String wx_miniPro_id) {
        this.wx_miniPro_id = wx_miniPro_id;
    }


    public String getWx_miniPro_path() {
        return wx_miniPro_path;
    }


    public void setWx_miniPro_path(String wx_miniPro_path) {
        this.wx_miniPro_path = wx_miniPro_path;
    }
    
}
