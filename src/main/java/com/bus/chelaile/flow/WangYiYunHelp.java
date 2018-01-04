package com.bus.chelaile.flow;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.bus.chelaile.flow.model.FlowChannel;
import com.bus.chelaile.flow.model.FlowContent;
import com.bus.chelaile.flow.wangyiyun.WangYIParamForSignature;
import com.bus.chelaile.flow.wangyiyun.WangYiYunChannelDto;
import com.bus.chelaile.flow.wangyiyun.WangYiYunDetailModel;
import com.bus.chelaile.flow.wangyiyun.WangYiYunErrorCode;
import com.bus.chelaile.flow.wangyiyun.WangYiYunInfoType;
import com.bus.chelaile.flow.wangyiyun.WangYiYunListDataDto;
import com.bus.chelaile.flow.wangyiyun.WangYiYunListModel;
import com.bus.chelaile.flow.wangyiyun.WangYiYunResultBaseDto;
import com.bus.chelaile.mvc.AdvParam;
import com.google.gson.reflect.TypeToken;

public class WangYiYunHelp extends AbstractWangYiYunHelp {

	@Override
	public List<FlowContent> getInfoByApi(AdvParam advParam, long ftime, String recoid, int channelId, boolean isShowAd)
			throws Exception {
		getChannelList("3");// 获取频道列表 现在选择默认推荐1852
		WangYiYunResultBaseDto<WangYiYunListDataDto> resultNewList = getNewList("3", "1852", advParam.getUdid());
		if (!resultNewList.getCode().equals(WangYiYunErrorCode.SUCCESS.getCode()))
			throw new Exception(gson.toJson(resultNewList));
		List<FlowContent> flowContentList = new ArrayList<>();
		for (WangYiYunListModel model : resultNewList.getData().getInfos()) {
			if (!model.getInfoType().equals(WangYiYunInfoType.ARTICLE.getWangYiType()))
				continue;
			WangYiYunResultBaseDto<WangYiYunDetailModel> resultDetailResult = getNewDetail("3", model.getInfoId());
			if (!resultDetailResult.getCode().equals(WangYiYunErrorCode.SUCCESS.getCode()))
				throw new Exception(gson.toJson(resultNewList));
			WangYiYunDetailModel wyDetailModel = resultDetailResult.getData();
			File des = new File(cdnPath + wyDetailModel.getInfoId() + ".html");
			
			// 写html文件
			String html = formatContentAndInstallHtml(modelFileName, wyDetailModel);
			FileUtils.writeStringToFile(des, html, "utf-8");

			flowContentList.add(parseNewDetailModelToFlowContent(wyDetailModel, newUrl + des.getName()));
		}

		return flowContentList;
	}

	private String formatContentAndInstallHtml(String modelHtml, WangYiYunDetailModel wyDetailModel)
			throws IOException {
		formatImagInContent(wyDetailModel);
		String html = installNewDetailHtml(modelHtml, wyDetailModel);
		return html;
	}

	private FlowContent parseNewDetailModelToFlowContent(WangYiYunDetailModel wangYiYunDetailModel, String url) {
		System.out.println(url);
		FlowContent flowContent = new FlowContent();
		flowContent.setType(0);
		flowContent.setTitle(wangYiYunDetailModel.getTitle());
		flowContent.setUrl(url);
		flowContent.setTime(wangYiYunDetailModel.getPublishTime().getTime());

		return flowContent;
	}

	@Override
	public List<String> parseChannel(FlowChannel ucChannel) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<FlowContent> parseResponse(AdvParam advParam, long ftime, String recoid, String token, String channelId,
			boolean isShowAd) {
		throw new UnsupportedOperationException();
	}

	private WangYiYunResultBaseDto<WangYiYunDetailModel> getNewDetail(String platform, String infoId) {
		Set<WangYIParamForSignature> paramSet = installNewDetailParam(platform, System.currentTimeMillis(), infoId,
				"recommendation");
		WangYiYunResultBaseDto<WangYiYunDetailModel> result = getWangYiYunResponse(wangYuYunNewDetailUrl, paramSet,
				new TypeToken<WangYiYunResultBaseDto<WangYiYunDetailModel>>() {
				}.getType());
		System.out.println(gsonFormat.toJson(result));
		log.info(gson.toJson(result));
		return result;
	}

	private WangYiYunResultBaseDto<WangYiYunListDataDto> getNewList(String plateForm, String channelId, String udid) {
		Set<WangYIParamForSignature> paramSet = installNewList(plateForm, System.currentTimeMillis(), channelId, udid);
		WangYiYunResultBaseDto<WangYiYunListDataDto> result = getWangYiYunResponse(wangYiYunNewListUrl, paramSet,
				new TypeToken<WangYiYunResultBaseDto<WangYiYunListDataDto>>() {
				}.getType());
		System.out.println(gsonFormat.toJson(result));
		log.info(gson.toJson(result));
		return result;
	}

	private WangYiYunResultBaseDto<WangYiYunChannelDto> getChannelList(String plateForm) {
		Set<WangYIParamForSignature> paramSet = initBaseParam(plateForm, System.currentTimeMillis());
		WangYiYunResultBaseDto<WangYiYunChannelDto> result = getWangYiYunResponse(wangYiYunChannelListUrl, paramSet,
				new TypeToken<WangYiYunResultBaseDto<WangYiYunChannelDto>>() {
				}.getType());
		System.out.println(gsonFormat.toJson(result));
		log.info(gson.toJson(result));
		return result;

	}

}
