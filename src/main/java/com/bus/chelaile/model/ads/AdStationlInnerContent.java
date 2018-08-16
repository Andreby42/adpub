package com.bus.chelaile.model.ads;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.bus.chelaile.model.ads.entity.AdEntity;
import com.bus.chelaile.model.ads.entity.TasksGroup;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.util.New;

/**
 * 推送的广告的内部的内容， 就是数据之中content的结构化表示。
 * 
 * @author liujh
 * 
 */
public class AdStationlInnerContent extends AdInnerContent {
	

    protected static final Logger logger = LoggerFactory.getLogger(AdStationlInnerContent.class);
	
    private String pic; // 广告图片的URL
    private AdCard adCard;
    private BannerInfo bannerInfo;
    private int buyOut; // 买断， 0 没有买断； 1 买断。 2018-03-29

    private int apiType;
    private int provider_id; // 广告提供商， 0：自采买广告；8：阅盟；9：Ecoook；10：科大讯飞【其他说明：3 inmobi】

    private long autoInterval; // 自动刷新时间
    private int adWeight; // 轮播权重
    private long mixInterval; // 最小展示时间
    private int backup; // 是否是备选方案
    private int clickDown; // 点击后排序到最后
    
    private String brandPic;    // 品牌车图片

    //  private String tasksStr; // tasks列表
    private List<TaskModel> tasksJ;
    private List<Long> timeouts; // 超时时间段设置

    private TasksGroup tasksGroup;
    
    private String adProducer;	//广告主

    @Override
    protected void parseJson(String jsonr) {
        AdStationlInnerContent ad = null;
        ad = JSON.parseObject(jsonr, AdStationlInnerContent.class);
        if (ad != null) {
            this.pic = ad.pic;
            this.setAdCard(ad.getAdCard());
            if (this.getAdCard() != null)
                this.getAdCard().setGpsType("gcj"); // 默认站点坐标取自高德地图的经纬度
            this.setBannerInfo(ad.getBannerInfo());
            this.adWeight = ad.getAdWeight();
            this.buyOut = ad.getBuyOut();
//            this.setWx_miniPro_id(ad.getWx_miniPro_id());
//            this.setWx_miniPro_path(ad.getWx_miniPro_path());
//            if(this.getWx_miniPro_id() != null)
//                this.setWx_miniPro_id(this.getWx_miniPro_id().trim());
//            if(this.getWx_miniPro_path() != null)
//                this.setWx_miniPro_path(this.getWx_miniPro_path().trim());
            this.setBrandPic(ad.getBrandPic());
            this.apiType = ad.apiType;
            this.provider_id = ad.getProvider_id();
            this.autoInterval = ad.autoInterval * 1000;
            this.mixInterval = ad.mixInterval * 1000;
            this.backup = ad.backup;
            this.clickDown = ad.clickDown;
            if( ad.getAdProducer() != null ) {
            	this.adProducer = ad.getAdProducer();
            }
            logger.info("adProducer={}",this.adProducer);

            this.setTasksJ(ad.getTasksJ());
            List<List<String>> tasksG = New.arrayList();
            
         
            Map<String,String> map = New.hashMap();
            
            if (this.getTasksJ() != null && this.getTasksJ().size() > 0) {
                Collections.sort(tasksJ, TaskModel_COMPARATOR);
                Set<Integer> prioritys = New.hashSet();
                for (TaskModel t : getTasksJ()) {
                	
                	map.put(t.getApiName(), t.getDisplayType()+"");
                	
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
                tasksGroups.setMap(map);
                this.tasksGroup = tasksGroups;
            } else if (provider_id < 2) {    // 如果tasks为空，设置默认的值，既车来了api
                this.tasksGroup = createOwnAdTask(ad);
            }
            
            setCommentContext(ad, this.pic);
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

    public static void main(String[] args) {
        AdStationlInnerContent adPush = new AdStationlInnerContent();

        adPush.setAndParseJson(
                "{\"pic\":\"https://image3.chelaile.net.cn/98949248b15141a9b5eb0759097b68eb\",\"bannerInfo\":{\"bannerType\":\"3\",\"name\":\"坚持打卡\",\"color\":\"174, 60, 60, 1\",\"slogan\":\"昨天喜欢你，今天喜欢你，明天看心情\",\"sloganColor\":\"29, 116, 113, 1\",\"tag\":{},\"button\":{\"buttonText\":\"测试\",\"buttonColor\":\"255, 255, 255, 1\",\"buttonBG\":\"84, 85, 25, 1\",\"buttonRim\":\"255, 0, 43, 1\",\"buttonPic\":\"\"}},\"adCard\":{\"open\":\"0\",\"cardType\":\"2\",\"logo\":\"\",\"topPic\":\"\",\"tagPic\":\"\",\"name\":\"\",\"address\":\"\",\"lng\":\"12.1\",\"lat\":\"\",\"phoneNum\":\"\",\"link\":\"\"}}");
        System.out.println("pic: " + adPush.pic);
        System.out.println("adCard: name " + adPush.getAdCard().getName());
        System.out.println("adCard: lng " + adPush.getAdCard().getLng());
        if (adPush.getAdCard().getLng() > 1.0) {
            System.out.println("lng 太大了");
        }
        System.out.println("JsonR: " + adPush.jsonContent);
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

    public AdCard getAdCard() {
        return adCard;
    }

    public void setAdCard(AdCard adCard) {
        this.adCard = adCard;
    }

    public BannerInfo getBannerInfo() {
        return bannerInfo;
    }

    public void setBannerInfo(BannerInfo bannerInfo) {
        this.bannerInfo = bannerInfo;
    }

    public int getAdWeight() {
        return adWeight;
    }

    public void setAdWeight(int adWeight) {
        this.adWeight = adWeight;
    }

    public int getBuyOut() {
        return buyOut;
    }

    public void setBuyOut(int buyOut) {
        this.buyOut = buyOut;
    }


    public int getProvider_id() {
        return provider_id;
    }

    public void setProvider_id(int provider_id) {
        this.provider_id = provider_id;
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

	public String getAdProducer() {
		return adProducer;
	}

	public void setAdProducer(String adProducer) {
		this.adProducer = adProducer;
	}

    public String getBrandPic() {
        return brandPic;
    }

    public void setBrandPic(String brandPic) {
        this.brandPic = brandPic;
    }

}
