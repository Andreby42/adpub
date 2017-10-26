package com.bus.chelaile.flowNew;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.flowNew.model.FlowContent;
import com.bus.chelaile.model.client.ClientDto;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.util.New;

public class FlowServiceManager {
	
	protected static final Logger logger = LoggerFactory.getLogger(FlowServiceManager.class);
	
	
	/**
	 * 初始化一些需要提前录入缓存的内容
	 * 除了‘热门文章’，其他都需要放入缓存中
	 */
	public void initFlows() {
		
		FlowStaticContents.initLineDetailFlows();
		FlowStaticContents.initArticleContents();
		
	}
	
	public String getClienSucMap(Object obj, String status) {
		ClientDto clientDto = new ClientDto();
		clientDto.setSuccessObject(obj, status);
		try {
			String json = JSON.toJSONString(clientDto, SerializerFeature.BrowserCompatible);
			// JsonBinder.toJson(clientDto, JsonBinder.always);
			return "**YGKJ" + json + "YGKJ##";
		} catch (Exception e1) {
			logger.error(e1.getMessage(), e1);
			return "";
		}
	}

	public String getClientErrMap(String errmsg, String status) {
		ClientDto clientDto = new ClientDto();
		clientDto.setErrorObject(errmsg, status);
		try {
			String json = JSON.toJSONString(clientDto);
			return "**YGKJ" + json + "YGKJ##";
		} catch (Exception e1) {
			logger.error(e1.getMessage(), e1);
			return "";
		}
	}
	
	
	public String getResponseLineDetailFlows(AdvParam param) {
		
		List<FlowContent> flows = getLineDetailFlows(param);
		if(flows != null && flows.size() > 0) {
			JSONObject responseJ = new JSONObject();
			responseJ.put("flows", flows);
			return getClienSucMap(responseJ, Constants.STATUS_REQUEST_SUCCESS);
		} else {
			return getClienSucMap(new JSONObject(), Constants.STATUS_REQUEST_SUCCESS);
		}
	}
	
	
	/**
	 * 获取详情页下方滚动栏内容
	 * @return
	 */
	public List<FlowContent> getLineDetailFlows(AdvParam param) {
		
		List<FlowContent> flows = New.arrayList();
		//条件判断
		if(isReturnLineDetailFlows(param)) {
		
			createList(FlowStaticContents.LINE_DETAIL_FLOWS, param, flows);
		}
		
		return flows;
		
	}
	
	
	
	
	
	
	/*
	 * 可能涉及到一些链接增加用户id之类的修正
	 */
	private List<FlowContent> createList(Map<Integer, List<FlowContent>> linedetailflows, AdvParam param, List<FlowContent> flows) {
		if(linedetailflows == null || linedetailflows.size() < 1) {
			logger.error("详情页下方滚动栏没有读取到任何内容！，udid={}", param.getUdid());
			return null;
		}
		
		// TODO ，需要更加科学详细的排序逻辑
		for(Integer type : linedetailflows.keySet()) {
			flows.addAll(linedetailflows.get(type));
		}
		return flows;
	}

	private boolean isReturnLineDetailFlows(AdvParam param) {
		// TODO 
		return true;
	}

	public static void main(String[] args) {
//		System.out.println(getClienSucMap(new JSONObject(), Constants.STATUS_REQUEST_SUCCESS));
	}
}
