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
		
//		if (showType.equalsIgnoreCase(ShowType.OPEN_SCREEN.getType())) {
//			res = new OpenAdEntity(ShowType.OPEN_SCREEN.getValue());
//			res.setShowType( ShowType.OPEN_SCREEN.getValue() );
//		} else if(showType.equalsIgnoreCase(ShowType.FULL_SCREEN.getType())){
//			res = new OpenAdEntity(ShowType.FULL_SCREEN.getValue());
//			res.setShowType( ShowType.FULL_SCREEN.getValue() );
//		} else if(showType.equalsIgnoreCase(ShowType.FULL_SCREEN_RIDE.getType())) {
//			res = new OpenAdEntity(ShowType.FULL_SCREEN_RIDE.getValue());
//			res.setShowType( ShowType.FULL_SCREEN_RIDE.getValue() );
//		}
		
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
//		res.setId(ad.getId());

//		Map<String, String> paramMap = new HashMap<String, String>();
//		paramMap.put(Constants.PARAM_AD_ID, String.valueOf(ad.getId()));
//		paramMap.put(Constants.PARAM_AD_TYPE, showType);

//		res.setLink(AdvUtil.buildRedirectLink(ad.getLink(), paramMap,
//				advParam.getUdid(), false, true,ad.getLink_extra()));
//		res.setMonitorType(ad.getMonitorType());
//		res.setUnfoldMonitorLink(AdvUtil.encodeUrl(ad.getUnfoldMonitorLink()));
//		res.setClickMonitorLink(AdvUtil.encodeUrl(ad.getClickMonitorLink()));
//
//		
//		res.setOpenType(ad.getOpenType());
//		res.setTargetType(ad.getTargetType());
//		res.setType(1);
		
		res.fillBaseInfo(ad, advParam, new HashMap<String, String>());
		res.setPlacementId("");

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
			if(fullInner.getTag() != null && fullInner.getTagId() != null) {
				res.setTag(new Tag(fullInner.getTag(), fullInner.getTagId()));
			}
				
		}
		if (res.getDuration() <= 0 || res.getDuration()  > 10) {
			res.setDuration(0);
			res.setIsDisplay(1);
		}

	//	res.dealUrl(advParam, ad.getMonitorType());
		
	}
	

}
