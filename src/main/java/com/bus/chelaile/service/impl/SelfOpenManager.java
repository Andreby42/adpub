package com.bus.chelaile.service.impl;

import java.util.HashMap;

import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.model.ads.AdContent;
import com.bus.chelaile.model.ads.AdFullInnerContent;
import com.bus.chelaile.model.ads.AdInnerContent;
import com.bus.chelaile.model.ads.Tag;
import com.bus.chelaile.model.ads.entity.OpenAdEntity;
import com.bus.chelaile.model.ads.entity.OpenOldAdEntity;
import com.bus.chelaile.mvc.AdvParam;


/**
 * 自采买的开屏
 * @author zzz
 *
 */
public class SelfOpenManager {
	/**
	 * 新版本开屏
	 * @param ad
	 * @param platform
	 * @param advParam
	 * @param showType
	 * @return
	 */
	public  OpenAdEntity from(AdContent ad,String platform, AdvParam advParam, String showType) {
		if (ad == null) {
			return null;
		}
		
		OpenAdEntity res = new OpenAdEntity(Integer.parseInt(showType));
		setEntity(ad, platform, advParam, showType, res);
		
		return res;
	}
	/**
	 * 旧版本开屏的返回多个时候
	 * @param ad
	 * @param platform
	 * @param advParam
	 * @param showType
	 * @return
	 */
	public  OpenOldAdEntity fromOld(AdContent ad,String platform, AdvParam advParam, String showType) {
		if (ad == null) {
			return null;
		}
		OpenOldAdEntity res = null;
		
		if (showType.equalsIgnoreCase(ShowType.OPEN_SCREEN.getType())) {
			res = new OpenOldAdEntity(ShowType.OPEN_SCREEN.getValue());
			res.setShowType( ShowType.OPEN_SCREEN.getValue() );
		} else {
			res = new OpenOldAdEntity(ShowType.FULL_SCREEN.getValue());
			res.setShowType( ShowType.FULL_SCREEN.getValue() );
		}
		setEntity(ad, platform, advParam, showType, res);
		
		return res;
	}
	
	private void setEntity(AdContent ad,String platform, AdvParam advParam, String showType,OpenAdEntity res){
	    
	       AdInnerContent inner = ad.getInnerContent();
	        if ((inner != null) && (inner instanceof AdFullInnerContent)) {
	            AdFullInnerContent fullInner =  (AdFullInnerContent)inner;
	            
	            res.setPic(res.getPicUrl(platform,
	                    fullInner.getIosURL(),
	                    fullInner.getAndroidURL(),
	                    fullInner.getPic()));
	            res.setIsSkip(fullInner.getIsSkip());
	            res.setIsDisplay(fullInner.getIsDisplay());
	            res.setDuration(fullInner.getDuration());
	            res.setIsFullShow(ad.getIs_fullScreen());
	            res.setFeedId(fullInner.getFeedId());
	            res.setTimeout(fullInner.getTimeout());
	            if(fullInner.getTag() != null && fullInner.getTagId() != null) {
	                res.setTag(new Tag(fullInner.getTag(), fullInner.getTagId()));
	            }
	            
	            if(fullInner.getTasksGroup() != null) {
	                res.setTasksGroup(fullInner.getTasksGroup());
	            }
	            
	                
	        }
	    
		
		res.fillBaseInfo(ad, advParam, new HashMap<String, String>());
		res.setPlacementId("");

		if (res.getDuration() <= 0 || res.getDuration()  > 10) {
			res.setDuration(0);
			res.setIsDisplay(1);
		}

	//	res.dealUrl(advParam, ad.getMonitorType());
		
	}
}
