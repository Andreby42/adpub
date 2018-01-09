package com.bus.chelaile.flow;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.common.AdvCache;
import com.bus.chelaile.common.CacheUtil;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.flow.model.FlowChannel;
import com.bus.chelaile.flow.model.FlowContent;
import com.bus.chelaile.flow.wangyiyun.WangYIParamForSignature;
import com.bus.chelaile.flow.wangyiyun.WangYiYunChannelDto;
import com.bus.chelaile.flow.wangyiyun.WangYiYunDetailModel;
import com.bus.chelaile.flow.wangyiyun.WangYiYunErrorCode;
import com.bus.chelaile.flow.wangyiyun.WangYiYunInfoType;
import com.bus.chelaile.flow.wangyiyun.WangYiYunListDataDto;
import com.bus.chelaile.flow.wangyiyun.WangYiYunListModel;
import com.bus.chelaile.flow.wangyiyun.WangYiYunListModel.Thumbnail;
import com.bus.chelaile.flow.wangyiyun.WangYiYunResultBaseDto;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.util.DateUtil;
import com.google.gson.reflect.TypeToken;

public class WangYiYunHelp extends AbstractWangYiYunHelp {

	@Override
	public List<FlowContent> getInfoByApi(AdvParam advParam, long ftime, String recoid, int channelId, boolean isShowAd)
			throws Exception {
		getChannelList("3");// 获取频道列表 现在选择默认推荐1852
		WangYiYunResultBaseDto<WangYiYunListDataDto> resultNewList = getNewList("3", "2663", advParam.getUdid());
		if (!resultNewList.getCode().equals(WangYiYunErrorCode.SUCCESS.getCode())) {
			log.error(gson.toJson(resultNewList));
			throw new Exception(gson.toJson(resultNewList));
		}
		List<FlowContent> flowContentList = new ArrayList<>();
		for (WangYiYunListModel model : resultNewList.getData().getInfos()) {
			if (!model.getInfoType().equals(WangYiYunInfoType.ARTICLE.getWangYiType())) {
				log.info("网易：获取不到是article类型的文章，infoType={}", model.getInfoType());
				continue;
			}
			WangYiYunResultBaseDto<WangYiYunDetailModel> resultDetailResult = getNewDetail("3", model.getInfoId());
			if (!resultDetailResult.getCode().equals(WangYiYunErrorCode.SUCCESS.getCode())) {
				log.info(gson.toJson(resultNewList));
				throw new Exception(gson.toJson(resultNewList));
			}
			WangYiYunDetailModel wyDetailModel = resultDetailResult.getData();
			
			// 判断该文章之前是否缓存过
			FlowContent flowContent;
			if(! Constants.ISTEST)		// TODO 测试暂时不缓存
				if ((flowContent = hasCached(wyDetailModel)) != null) {
					flowContentList.add(flowContent);
					continue;
				}
			
			// 写html文件
			String date = DateUtil.getTodayStr("yyyy-MM-dd");
			File path = new File(cdnPath + date);
            if (!path.exists()) {
                boolean createFlag = path.mkdirs();
                log.info("网易：create filePath {} : {}", path, createFlag);
            }
			
			File des = new File(path + "/" + wyDetailModel.getInfoId() + ".html");
			String html = formatContentAndInstallHtml(modelFileName, wyDetailModel);
//			System.out.println("###################### " + html);
			html = html.replaceAll("&lt;", "<");
			html = html.replaceAll("&gt;", ">");
			FileUtils.writeStringToFile(des, html, "utf-8");

			flowContentList.add(parseNewDetailModelToFlowContent(wyDetailModel, model.getThumbnails(), newUrl + "/" + date + "/" + des.getName()));
//			flowContentList.add(parseNewDetailModelToFlowContent(wyDetailModel, newUrl));
		}
		log.info("网易：获取到文章数目: {}", flowContentList.size());
		log.info("从api获取到的content结构是：{}", JSONObject.toJSONString(flowContentList));
		return flowContentList;
	}

	private FlowContent hasCached(WangYiYunDetailModel wyDetailModel) {
		FlowContent flowContent = new FlowContent();
		String flowCachedKey = AdvCache.getWangyiArticleCacheKey(wyDetailModel.getInfoId());
		String flowStr = (String) CacheUtil.get(flowCachedKey);
		if(flowStr == null) {
			return null;
		}
		flowContent = JSONObject.parseObject(flowStr, FlowContent.class);
		log.info("网易：获取到缓存过的文章，id={}", flowContent.getId());
		return flowContent;
	}

	private String formatContentAndInstallHtml(String modelHtml, WangYiYunDetailModel wyDetailModel)
			throws IOException {
		formatImagInContent(wyDetailModel);
		String html = installNewDetailHtml(modelHtml, wyDetailModel);
		return html;
	}

	private FlowContent parseNewDetailModelToFlowContent(WangYiYunDetailModel wangYiYunDetailModel, List<Thumbnail> imgs, String url) {
//		System.out.println(url);
		FlowContent flowContent = new FlowContent();
		flowContent.setType(0);
		flowContent.setTitle(wangYiYunDetailModel.getTitle());
		flowContent.setUrl(url);
		flowContent.setId(wangYiYunDetailModel.getInfoId());
		flowContent.setTime(DateUtil.getDate(wangYiYunDetailModel.getUpdateTime(), "yyyy-MM-dd HH:mm:ss").getTime());  // 时间问题
		flowContent.setImgs(wangYiYunDetailModel.createImgs(imgs));
		flowContent.setDesc(wangYiYunDetailModel.getSource());

		String flowCachedKey = AdvCache.getWangyiArticleCacheKey(flowContent.getId());
		log.info("网易：缓存文章,id={}", flowContent.getId());
		CacheUtil.set(flowCachedKey, Constants.SEVEN_DAY_TIME, JSONObject.toJSONString(flowContent));
		
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
//		System.out.println(gsonFormat.toJson(result));
//		log.info(gson.toJson(result));
		return result;
	}

	private WangYiYunResultBaseDto<WangYiYunListDataDto> getNewList(String plateForm, String channelId, String udid) {
		Set<WangYIParamForSignature> paramSet = installNewList(plateForm, System.currentTimeMillis(), channelId, udid);
		WangYiYunResultBaseDto<WangYiYunListDataDto> result = getWangYiYunResponse(wangYiYunNewListUrl, paramSet,
				new TypeToken<WangYiYunResultBaseDto<WangYiYunListDataDto>>() {
				}.getType());
//		System.out.println(gsonFormat.toJson(result));
//		log.info(gson.toJson(result));
		return result;
	}

	private WangYiYunResultBaseDto<WangYiYunChannelDto> getChannelList(String plateForm) {
		Set<WangYIParamForSignature> paramSet = initBaseParam(plateForm, System.currentTimeMillis());
		WangYiYunResultBaseDto<WangYiYunChannelDto> result = getWangYiYunResponse(wangYiYunChannelListUrl, paramSet,
				new TypeToken<WangYiYunResultBaseDto<WangYiYunChannelDto>>() {
				}.getType());
//		System.out.println(gsonFormat.toJson(result));
//		log.info(gson.toJson(result));
		return result;

	}

}
