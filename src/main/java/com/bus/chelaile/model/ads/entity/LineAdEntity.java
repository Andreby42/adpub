package com.bus.chelaile.model.ads.entity;





import java.util.HashMap;

import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.model.ads.AdContent;
import com.bus.chelaile.model.rule.version.VersionEntity;
import com.bus.chelaile.mvc.AdvParam;





public class LineAdEntity extends BaseAdEntity {

    private String                    pic = EMPTY_STR;                                                        // 图片URL
    private String                    combpic;                                                    // 组合图片的链接。
    private int                       adMode;
    private int						  idPraiseCount;											  // 广告详情页浮层点赞数量
    private boolean                   isPraise;													  //广告详情页浮层是否点赞过，false 未点赞，true 已点赞
 //   private String					  provider_id = "1";										  //我们自己广告
    public static final VersionEntity ANDROID_DETAIL_ADMODE_VERSION = new VersionEntity(3, 13, 0);
    public static final VersionEntity IOS_DETAIL_ADMODE_VERSION     = new VersionEntity(5, 11, 0);
    public static final VersionEntity BASE_VERSION                  = new VersionEntity(1, 0, 0);
  //  private int type = 1;


    public LineAdEntity(){
        super(ShowType.LINE_DETAIL.getValue());
        this.pic = EMPTY_STR;
        this.combpic = EMPTY_STR;
    }

  
    public void fillInfo(AdContent ad, AdvParam advParam){
    	this.fillBaseInfo(ad, advParam, new HashMap<String, String>());
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("LineAdEntity(super=");
        sb.append(super.toString()).append(", pic=").append(pic)
            .append(",adMode=").append(adMode)
            .append(", combpic=").append(combpic)
            .append(")");
        
        return sb.toString();
    }
    
//    public String getString() {
//        StringBuffer sb = new StringBuffer("LineAdEntity(super=");
//        sb.append(super.toString()).append(", pic=").append(pic)
//            .append(",adMode=").append(adMode)
//            .append(", combpic=").append(combpic)
//            .append(",link=").append(link)
//            .append(",openType=").append(openType)
//            .append(",targetType=").append(targetType)
//            .append(",showType=").append(showType)
//            .append(",id=").append(id)
//            .append(",idPraiseCount=").append(idPraiseCount)
//            .append(",isPraise=").append(isPraise)
//            .append(")");
//        
//        return sb.toString();
//    }

 
 

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public String getCombpic() {
		return combpic;
	}

	public void setCombpic(String combpic) {
		this.combpic = combpic;
	}

	public int getAdMode() {
		return adMode;
	}

	public void setAdMode(int adMode) {
		this.adMode = adMode;
	}

	public int getIdPraiseCount() {
		return idPraiseCount;
	}

	public void setIdPraiseCount(int idPraiseCount) {
		this.idPraiseCount = idPraiseCount;
	}

	public boolean isPraise() {
		return isPraise;
	}

	public void setPraise(boolean isPraise) {
		this.isPraise = isPraise;
	}





	@Override
	protected ShowType gainShowTypeEnum() {
		if(showType == ShowType.LINE_DETAIL.getValue()){
			return ShowType.LINE_DETAIL;
		}else if(showType == 7){
			return ShowType.ACTIVE_DETAIL;
		}else if(showType == 8){
			return ShowType.RIDE_DETAIL;
		}else {
			return ShowType.RIDE_AUDIO;
		}
	}
    
    
}
