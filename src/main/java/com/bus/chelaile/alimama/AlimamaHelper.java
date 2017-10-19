package com.bus.chelaile.alimama;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.alimama.response.AdContent;
import com.bus.chelaile.alimama.response.AlimamaResponse;
import com.bus.chelaile.alimama.response.Creative;
import com.bus.chelaile.innob.AdvType;
import com.bus.chelaile.innob.ApiType;
import com.bus.chelaile.innob.ProductType;
import com.bus.chelaile.model.ads.entity.ApiLineEntity;





public class AlimamaHelper {
	
	protected static final Logger logger = LoggerFactory.getLogger(AlimamaHelper.class);
	
	private AlimamaRequestManager manager = new AlimamaRequestManager();

	public ApiLineEntity getAndroidInfo(String net,int vc,String ip,String deviceType,String s,String sv,String imei,String udid) throws Exception{
		AlimamaResponse res = null;
		try{
			res = manager.getAlimamaResponse(net, vc+"", ip, deviceType, s, sv, null, imei);
		}catch( Exception e ){
			logger.info("udid={},net={},vc={},ip={},dt={},s={},sv={},e={}",udid,net,vc,ip,deviceType,s,sv,e.getMessage());
			throw e;
		}
		return getApiLineEntityByResponse(res, udid);
	}
	
	public ApiLineEntity getIosInfo(String net,int vc,String ip,String deviceType,String s,String sv,String idfa,String udid) throws Exception{
		AlimamaResponse res = null;
		try{
			res = manager.getAlimamaResponse(net, vc+"", ip, deviceType, s, sv, idfa, null);
		}catch( Exception e ){
			logger.info("udid={},net={},vc={},ip={},dt={},s={},sv={},e={}",udid,net,vc,ip,deviceType,s,sv,e.getMessage());
			throw e;
		}
		
		return getApiLineEntityByResponse(res, udid);
	}
	
	
	private ApiLineEntity getApiLineEntityByResponse(AlimamaResponse response,String udid) throws IOException{
		if( !response.getStatus().equalsIgnoreCase("ok") ){
			logger.info("udid={},errcode={}",udid,response.getErrcode());
			throw new IllegalArgumentException("status错误");
		}
		ApiLineEntity entity = new ApiLineEntity();
		entity.setType(AdvType.API.getValue());
		entity.setProvider_id(ProductType.AILIMAMA.getValue());
		entity.setApiType(ApiType.NATIVE.getValue());
		AdContent ad = response.getAd().get(0);
		Creative cr = ad.getCreative().get(0);
		
		if( ad.getTid() == 2 ){
			entity.setApiDes(cr.getMedia().getAd_words());
			entity.setApiTitle( cr.getMedia().getTitle() );
		}else	if( ad.getTid() == 1 ){
			entity.setApiDes("");
			entity.setApiType(ApiType.IMG.getValue());
		}else	if( ad.getTid() == 9  ){
			entity.setApiDes("");
			entity.setApiTitle( cr.getMedia().getTitle() );
		}
		else{
			logger.info("udid={},tid={}",udid,ad.getTid());
			throw new IllegalArgumentException("tid错误");
		}
		
		if( cr.getMedia().getClick_url() != null && !cr.getMedia().getClick_url().equals("") ){
			entity.setLink(cr.getMedia().getClick_url());
		}else	if( cr.getMedia().getDownload_url() != null && !cr.getMedia().getClick_url().equals("")  ){
			entity.setLink(cr.getMedia().getDownload_url());
		}
		
		if( ad.getSet().getAtype() != 12 ){
			logger.info("udid={},atype={}",udid,ad.getSet().getAtype());
			throw new IllegalArgumentException("adType错误");
		}
		
		
		List<String> unfoldList = cr.getImpression();
		List<String> clickList = cr.getClick();
		
		entity.setCombpic(cr.getMedia().getImg_url());
		
		
		//	打开应用
		if( cr.getMedia().getEvent() == 1 ){
			entity.setOpenType(0);
		}else	if( cr.getMedia().getEvent() == 2 ){
			entity.setOpenType(1);
		}else{
			logger.info("udid={},event={}",udid,cr.getMedia().getEvent());
			throw new IllegalArgumentException("event错误");
		}
		
		if( unfoldList == null || unfoldList.size() == 0 ){
			throw new IllegalArgumentException("展示的链接为空");
		}
		entity.setUnfoldMonitorLink(splitList(unfoldList));
		entity.setId(ad.getAid());
		if( clickList == null || clickList.size() == 0 ){
			return entity;
			//throw new IllegalArgumentException("");
		}
		
		entity.setClickMonitorLink(splitList(clickList));
		
		return entity;
	}
	
	private static String splitList(List<String> list){

		String url="";
		
		for( String str : list ){
			
			url += str;
			
			url += ";";
			
		}
		
		return url.substring(0, url.length() - 1);
		
	}
	
}
