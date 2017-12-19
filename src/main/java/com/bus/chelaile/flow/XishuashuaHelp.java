package com.bus.chelaile.flow;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.bus.chelaile.common.AnalysisLog;
import com.bus.chelaile.common.CacheUtil;
import com.bus.chelaile.model.ArticlesType;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.flow.model.ArticleInfo;
import com.bus.chelaile.flow.model.ChannelType;
import com.bus.chelaile.flow.model.FlowChannel;
import com.bus.chelaile.flow.model.FlowContent;
import com.bus.chelaile.flow.uc.ArticleRecomResponse;
import com.bus.chelaile.flow.uc.ArticleResponse;
import com.bus.chelaile.flow.uc.ArticleResponseData;
import com.bus.chelaile.flow.uc.Item;
import com.bus.chelaile.flow.uc.ItemsTable;
import com.bus.chelaile.flow.uc.UcResponse;
import com.bus.chelaile.util.DateUtil;
import com.bus.chelaile.util.HttpUtils;
import com.bus.chelaile.util.New;
import com.bus.chelaile.model.PropertiesName;
import com.bus.chelaile.util.config.PropertiesUtils;

public class XishuashuaHelp implements InterfaceFlowHelp {

	@Autowired
	private ActivityService activityService;

	private static final Logger logger = LoggerFactory.getLogger(XishuashuaHelp.class);
	private static final String requestUrl = PropertiesUtils
			.getValue(
					PropertiesName.PUBLIC.getValue(),
					"uc.xishuashua.request.url",
					"http://web.chelaile.net.cn/openiflow/openapi/v1/channel/%s?app=chelaile-iflow&method=%s&ftime=%s&recoid=%s&imei=867601025050011&ve=3.3.8&fr=%s&access_token=%s&dn=%s");

	private static final String articleINfoUrl = PropertiesUtils.getValue(PropertiesName.PUBLIC.getValue(),
			"uc.xishuashua.articleINfo.url", "http://open.uczzd.cn/openarticle/openapi/v1/article/%s?access_token=%s");

	private static final String articleRecomUrl = "http://open.uczzd.cn/openarticle/openapi/v1/article/%s/related?cid=100&access_token=%s";

	/*
	 * 从喜刷刷接口获得内容
	 */
	@Override
	public List<FlowContent> getInfoByApi(AdvParam advParam, long ftime, String recoid, int id, boolean isShowAd) throws Exception {
		String token = (String) CacheUtil.getApiInfo("xishuashuatoken");
		if (token == null) {
			logger.error("洗刷刷token为空");
			return null;
		}
		// token =
		// "1489428602505-ece29b313328581a74ff847d8cfc6205-38d8ce53c6051e87bf0136cdf586cba2";

		// 读取channelId，调用每个channel
		FlowChannel ucChannel = activityService.getChannels(id, ChannelType.UC);
		List<String> apiChannelIds = parseChannel(ucChannel);

		if (id == -1 || apiChannelIds == null || apiChannelIds.size() == 0) {
			logger.info("没有读取到有效的channelIds，用默认的推荐频道");
			AnalysisLog
			.info("[GET_UC_ARTICLES]: accountId={}, udid={}, cityId={}, s={}, v={}, lineId={}, nw={},ip={},deviceType={},geo_lng={},geo_lat={},stats_act={},channelId={},refer={}",
					advParam.getAccountId(), advParam.getUdid(), advParam.getCityId(), advParam.getS(),
					advParam.getV(), advParam.getLineId(), advParam.getNw(), advParam.getIp(),
					advParam.getDeviceType(), advParam.getLng(), advParam.getLat(), advParam.getStatsAct(), 1,advParam.getRefer());
			return parseResponse(advParam, ftime, recoid, token, "100", true); // 默认
		} else {
			int contentNumber = 21 / apiChannelIds.size();
			List<FlowContent> UcContents = new ArrayList<>();
			for (String chennelId : apiChannelIds) {
				List<FlowContent> reponseUcContent = parseResponse(advParam, ftime, recoid, token, chennelId, true);
				if (reponseUcContent != null && reponseUcContent.size() > 0) {
					UcContents.addAll(reponseUcContent.subList(0,
							contentNumber >= reponseUcContent.size() ? reponseUcContent.size() - 1 : contentNumber));
				} else {
					logger.error("出现频道返回为空的情况！ channelId={}", chennelId);
				}
			}
			AnalysisLog
			.info("[GET_UC_ARTICLES]: accountId={}, udid={}, cityId={}, s={}, v={}, lineId={}, nw={},ip={},deviceType={},geo_lng={},geo_lat={},stats_act={},channelId={},refer={}",
					advParam.getAccountId(), advParam.getUdid(), advParam.getCityId(), advParam.getS(),
					advParam.getV(), advParam.getLineId(), advParam.getNw(), advParam.getIp(),
					advParam.getDeviceType(), advParam.getLng(), advParam.getLat(), advParam.getStatsAct(), id ,advParam.getRefer());
			
			return UcContents;
		}
	}

	/*
	 * 调用接口获取数据
	 */
	@Override
	public List<FlowContent> parseResponse(AdvParam advParam, long ftime, String recoid, String token, String channelId, boolean isShowAd) {
		List<FlowContent> contents = New.arrayList();
		String url = null;
		String response = null;
		if (ftime == 0 || ftime == -1 || recoid == null) {
			url = String.format(requestUrl, channelId, "new", 0, null, advParam.getS(), token, advParam.getUdid());
		} else {
			url = String
					.format(requestUrl, channelId, "his", ftime, recoid, advParam.getS(), token, advParam.getUdid());
		}
		if(channelId.equals("200")) {
			url += "&city_name=" + activityService.getCityName(advParam.getCityId());
		}
		
		try {
			logger.info("channelId={}", channelId);
			logger.info("洗刷刷接口url={}", url);
			response = HttpUtils.get(url, "UTF-8");
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		if (response == null) {
			logger.error("洗刷刷返回为空： url={}, response= {}", url, response);
			return null;
		}

		UcResponse ucRes = JSON.parseObject(response, UcResponse.class);
		if (ucRes.getStatus() != 0 || ucRes.getData() == null || ucRes.getData().getItems() == null) {
			logger.error("洗刷刷返回内容异常： url={}, response= {}", url, response);
			return null;
		}
		ArrayList<Item> itemList = new ArrayList<Item>();
		for (ItemsTable table : ucRes.getData().getItems()) {
			String id = table.getId();
			String map = table.getMap();
			if (map.equals(ArticlesType.ARTICLES.getType())) {
				Item article = ucRes.getData().getArticleByTableId(id);
				if (article != null) {
					itemList.add(article);
				}
			} else if (map.equals(ArticlesType.SPECIALS.getType())) {
				List<Item> specials = ucRes.getData().getSpecialListByTableId(id);
				if (itemList != null && itemList.size() > 0) {
					itemList.addAll(specials);
				}
			} else {
				logger.error("出现未知类型的文章：type={}, respones={}", map, response);
			}
		}

		contents = dealItem(itemList, advParam);
		Collections.sort(contents, new Comparator<FlowContent>() {		//排序，按照文章发布时间
			@Override
			public int compare(FlowContent o1, FlowContent o2) {
				if (o1 == null)
					return -1;
				if (o2 == null)
					return 1;

				return (int) (o2.getTime() - o1.getTime());
			}
		});
//		setImagsType(contents);	 //设置大小图模式
		
		
		return contents;
	}

	@Override
	public List<String> parseChannel(FlowChannel ucChannel) {
		if (ucChannel == null) {
			return null;
		}
		List<String> ids = new ArrayList<String>();
		for (String id : ucChannel.getChannelId().split("&")) {
				ids.add(id);
		}
		return ids;
	}
	
	private ArrayList<FlowContent> dealItem(ArrayList<Item> itemList, AdvParam advParam) {
		ArrayList<FlowContent> ucList = new ArrayList<FlowContent>();
		for (Item it : itemList) {
			// 没有图片的删掉
			if (it.getThumbnails() == null || it.getThumbnails().size() == 0) {
				continue;
			}

			FlowContent uc = new FlowContent();
			uc.setId(it.getId());
			uc.setTime(it.getGrab_time());
			uc.setImgs(it.delThumbnails(advParam));
			uc.setDesc(it.getOrigin_src_name());
			uc.setTitle(it.getTitle());
			uc.setRecoid(it.getRecoid());
			uc.setUrl(it.delUrl(advParam));
			if(it.getThumbnails().size() >= 3) {
				uc.setImgsType(2);	//三图小图
			} else {
				uc.setImgsType(0);	//单图小图
			}
			ucList.add(uc);
		}
		return ucList;
	}

	/*
	 * 从洗刷刷接口获取文章详情
	 */
	public ArticleInfo getArticleInfo(String id) {
		String token = (String) CacheUtil.getApiInfo("xishuashuatoken");
		if (token == null) {
			logger.error("洗刷刷token为空");
			return null;
		}

		String url = String.format(articleINfoUrl, id, token);
		String response = null;
		try {
			response = HttpUtils.get(url, "UTF-8");
			logger.info("头条返回的文章详情：{}", response); //TODO 
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		if (response == null) {
			logger.error("文章详情页返回为空： url={}, response= {}", url, response);
			return null;
		}

		ArticleResponse articleRes = JSON.parseObject(response, ArticleResponse.class);
		if (articleRes.getStatus() != 0 || articleRes.getData() == null || articleRes.getData().getContent() == null
				|| articleRes.getData().getImages() == null) {
			logger.error("获取文章出错,response={}", response);
			return null;
		}

		ArticleInfo articleInfo = new ArticleInfo();
		articleInfo.setTitle(articleRes.getData().getTitle());
		try {
			articleInfo.setDesc(DateUtil.getFormatTime(new Date(articleRes.getData().getGrab_time()), "MM-dd"));
		} catch (ParseException e) {
			logger.error("时间转换出错,response={}", response);
			e.printStackTrace();
			return null;
		}
		articleInfo.setAuthor(articleRes.getData().getOrigin_src_name());
		articleInfo.setContent(articleRes.getData().handleContent());
		articleInfo.setImgRtio(articleRes.getData().handleImgRtio());
		articleInfo.setShareDesc(articleRes.getData().handlShareDesc());

		return articleInfo;
	}

	/*
	 * 从洗刷刷接口获取相关推荐文章
	 */
	public List<ArticleInfo> getArticleRecom(String articleId, AdvParam advParam) {
		String token = (String) CacheUtil.getApiInfo("xishuashuatoken");
		if (token == null) {
			logger.error("洗刷刷token为空");
			return null;
		}

		String url = String.format(articleRecomUrl, articleId, token);
		String response = null;
		try {
			response = HttpUtils.get(url, "UTF-8");
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		if (response == null) {
			logger.error("文章相关推荐页返回为空： url={}, response= {}", url, response);
			return null;
		}

		ArticleRecomResponse articleRecomResponse = JSON.parseObject(response, ArticleRecomResponse.class);
		if (articleRecomResponse == null || articleRecomResponse.getData().getStatus() != 0
				|| articleRecomResponse.getData() == null) {
			logger.error("获取相关推荐出错,response={}", response);
			return null;
		}

		List<ArticleResponseData> articleRecomList = articleRecomResponse.getData().getArticles();
		List<ArticleInfo> articles = new ArrayList<ArticleInfo>();
		for (ArticleResponseData ar : articleRecomList) {
			if (ar.getItem_type() != 0 && ar.getItem_type() != 1) {
				logger.error("推荐文章中出现item_type不为0和1的值，原文id={}", articleId);
				continue;
			}

			ArticleInfo articleInfo = new ArticleInfo();
			articleInfo.setTitle(ar.getTitle());
			articleInfo.setDesc(DateUtil.flowFormatTime(ar.getGrab_time() + ""));
			articleInfo.setAuthor(ar.getOrigin_src_name());
			articleInfo.setUrl(ar.handleUrl(advParam));
			articleInfo.setImgUrl(ar.handleImgUrls());

			if (articleInfo.getImgUrl() == null) {
				continue;
			}
			articles.add(articleInfo);
		}
		return articles;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// String response =
		// "{\"data\":{\"articles\":[{\"id\":\"6924576370590604436\",\"recoid\":\"\",\"title\":\"她长相平庸却成豪门媳妇, 身价百亿却戴摆摊货!\",\"subhead\":\"\",\"url\":\"http://m.uczzd.cn/webview/news?app=chelaile-iflow&aid=6924576370590604436&cid=100&zzd_from=chelaile-iflow&uc_param_str=dndsfrvesvntnwpfgicp&recoid=&rd_type=reco&sp_gz=0\",\"hyperlinks\":[],\"strategy\":255,\"politics\":0,\"summary\":\"\",\"content\":\"\",\"thumbnails\":[{\"url\":\"http://image.uczzd.cn/7799657172539570064.jpeg?id=0\",\"width\":640,\"height\":426,\"type\":\"jpeg\",\"preload\":0,\"daoliu_url\":\"\",\"daoliu_title\":\"\"},{\"url\":\"http://image.uczzd.cn/16645184534164131512.jpeg?id=0\",\"width\":485,\"height\":549,\"type\":\"jpeg\",\"preload\":0,\"daoliu_url\":\"\",\"daoliu_title\":\"\"},{\"url\":\"http://image.uczzd.cn/4581974071281147637.jpeg?id=0\",\"width\":605,\"height\":330,\"type\":\"jpeg\",\"preload\":0,\"daoliu_url\":\"\",\"daoliu_title\":\"\"},{\"url\":\"http://image.uczzd.cn/667233122816084096.jpeg?id=0\",\"width\":384,\"height\":495,\"type\":\"jpeg\",\"preload\":0,\"daoliu_url\":\"\",\"daoliu_title\":\"\"}],\"images\":[{\"title\":\"\",\"url\":\"http://image.uczzd.cn/7799657172539570064.jpeg?id=0\",\"description\":\"\",\"index\":0,\"width\":640,\"height\":426,\"preload\":1,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"http://n.sinaimg.cn/sinacn/20170308/98a3-fycapec3445889.jpg\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/16645184534164131512.jpeg?id=0\",\"description\":\"\",\"index\":1,\"width\":485,\"height\":549,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/4581974071281147637.jpeg?id=0\",\"description\":\"\",\"index\":2,\"width\":605,\"height\":330,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/667233122816084096.jpeg?id=0\",\"description\":\"\",\"index\":3,\"width\":384,\"height\":495,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/3606074443621462298.jpeg?id=0\",\"description\":\"\",\"index\":4,\"width\":535,\"height\":594,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/3298491350491290008.jpeg?id=0\",\"description\":\"\",\"index\":5,\"width\":640,\"height\":825,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/14052946444711966620.jpeg?id=0\",\"description\":\"\",\"index\":6,\"width\":390,\"height\":220,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0}],\"album\":{\"intro\":\"\",\"enabled\":false,\"cover_image\":\"\",\"img_pg_type\":0},\"tags\":[\"郭晶晶\",\"霍启刚\"],\"audios\":[],\"category\":[\"娱乐\",\"明星\"],\"videos\":[],\"cid\":179223212,\"contentExtHyperlinks\":[],\"contentExtVotes\":[],\"subArticle\":false,\"title_icon\":\"\",\"grab_time\":1488944446000,\"clickable_url\":true,\"item_type\":0,\"style_type\":0,\"op_mark\":\"\",\"op_mark_icolor\":0,\"op_mark_iurl\":\"\",\"editor_icon\":\"\",\"editor_nickname\":\"\",\"op_info\":\"\",\"post_like_url\":\"\",\"post_dislike_url\":\"\",\"app_download_url\":\"\",\"app_download_desc\":\"\",\"download_type\":\"\",\"site_logo\":{\"id\":0,\"style\":2,\"desc\":\"新浪\",\"link\":\"\",\"img\":{\"url\":\"\",\"width\":0,\"height\":0,\"type\":\"\",\"preload\":0,\"daoliu_url\":\"\",\"daoliu_title\":\"\"}},\"enable_dislike\":true,\"is_drop_down_style\":false,\"dislike_infos\":[{\"type\":0,\"code\":1,\"msg\":\"重复推荐\",\"with_review\":0},{\"type\":0,\"code\":2,\"msg\":\"郭晶晶\",\"with_review\":0},{\"type\":0,\"code\":4,\"msg\":\"娱乐\",\"with_review\":0},{\"type\":0,\"code\":3,\"msg\":\"明星\",\"with_review\":0},{\"type\":0,\"code\":5,\"msg\":\"来源:新浪\",\"with_review\":0},{\"type\":1,\"code\":13,\"msg\":\"内容低俗\",\"with_review\":0},{\"type\":1,\"code\":16,\"msg\":\"标题夸张\",\"with_review\":0},{\"type\":1,\"code\":11,\"msg\":\"广告软文\",\"with_review\":0}],\"reco_desc\":\"\",\"news_poi_mark\":{},\"item_ext_bar\":{},\"source_name\":\"新浪\",\"origin_src_name\":\"青青电影\",\"publish_time\":1488942540000,\"content_type\":0,\"daoliu_type\":0,\"original_url\":\"http://k.sina.cn/article_5777373348_1585bb8a40010028so.html\",\"content_length\":1296,\"cmt_cnt\":0,\"load_priority\":0,\"use_cache\":0,\"fresh_time\":0,\"article_like_cnt\":38,\"like_cnt\":0,\"dislike_cnt\":0,\"view_cnt\":0,\"share_cnt\":0,\"read_id\":\"12586729655337808297\",\"query_tags\":[{\"tag\":\"郭晶晶\",\"url\":\"\"},{\"tag\":\"霍启刚\",\"url\":\"\"}],\"video_tags\":[{\"real_tag\":\"郭晶晶\",\"style\":\"\",\"url\":\"\",\"alias_tag\":\"\"},{\"real_tag\":\"霍启刚\",\"style\":\"\",\"url\":\"\",\"alias_tag\":\"\"}],\"show_impression_url\":\"\",\"wm_author\":{},\"participant_cnt\":0,\"avg_score\":0,\"matched_tags\":[],\"character_cards\":[],\"hot_cmts\":[],\"cmt_url\":\"http://m.uczzd.cn/webview/xissAllComments?app=chelaile-iflow&aid=6924576370590604436&cid=100&zzd_from=chelaile-iflow&uc_param_str=dndsvebichfrntcpgipf&uc_biz_str=S:custom|C:comment|N:true\",\"cmt_enabled\":true,\"redirect_ch_id\":0,\"redirect_ch_name\":\"\",\"redirect_ch_img\":\"\",\"redirect_ch_desc\":\"\",\"zzd_url\":\"http://m.uczzd.cn/webview/news?app=chelaile-iflow&aid=6924576370590604436&cid=100&zzd_from=chelaile-iflow&uc_param_str=dndsfrvesvntnwpfgicp&recoid=&rd_type=reco&sp_gz=0\",\"is_wemedia\":false,\"share_url\":\"http://s4.uczzd.cn/webview/news?app=chelaile-iflow&aid=6924576370590604436&cid=100&zzd_from=chelaile-iflow&uc_param_str=dndsfrvesvntnwpfgicp&recoid=&rd_type=share&sp_gz=0&pagetype=share&btifl=100\",\"ad_content\":{},\"qiqu_info\":{},\"title_img_hyperlink\":{},\"content_ext_audios\":[],\"reco_reason_type\":-1,\"reco_reason\":\"\",\"reco_tag_desc\":\"\",\"video_immersive_mode\":false,\"daoliu_switch\":{},\"event_tag_info\":[]},{\"id\":\"12857290119362313440\",\"recoid\":\"\",\"title\":\"郭晶晶一个细节被赞, 嫁入豪门仍然不忘初心\",\"subhead\":\"\",\"url\":\"http://m.uczzd.cn/webview/news?app=chelaile-iflow&aid=12857290119362313440&cid=100&zzd_from=chelaile-iflow&uc_param_str=dndsfrvesvntnwpfgicp&recoid=&rd_type=reco&sp_gz=0\",\"hyperlinks\":[],\"strategy\":255,\"politics\":0,\"summary\":\"\",\"content\":\"\",\"thumbnails\":[{\"url\":\"http://image.uczzd.cn/15949517166018437599.jpeg?id=0\",\"width\":426,\"height\":442,\"type\":\"jpeg\",\"preload\":0,\"daoliu_url\":\"\",\"daoliu_title\":\"\"}],\"images\":[{\"title\":\"\",\"url\":\"http://image.uczzd.cn/15949517166018437599.jpeg?id=0\",\"description\":\"\",\"index\":0,\"width\":426,\"height\":442,\"preload\":1,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"https://image.ynet.cn/2017/03/10/929203677be246feafde108857ba5e17_480x-_60.jpg\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/12335585167707469436.jpeg?id=0\",\"description\":\"\",\"index\":1,\"width\":388,\"height\":389,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/15927508313055711969.jpeg?id=0\",\"description\":\"\",\"index\":2,\"width\":374,\"height\":369,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/14035026562219858977.jpeg?id=0\",\"description\":\"\",\"index\":3,\"width\":480,\"height\":304,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/6243303527613542144.jpeg?id=0\",\"description\":\"\",\"index\":4,\"width\":432,\"height\":385,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0}],\"album\":{\"intro\":\"\",\"enabled\":false,\"cover_image\":\"\",\"img_pg_type\":0},\"tags\":[\"郭晶晶\",\"嫁入豪门\"],\"audios\":[],\"category\":[\"娱乐\",\"明星\"],\"videos\":[],\"cid\":200,\"contentExtHyperlinks\":[],\"contentExtVotes\":[],\"subArticle\":false,\"title_icon\":\"\",\"grab_time\":1489123139000,\"clickable_url\":true,\"item_type\":0,\"style_type\":0,\"op_mark\":\"\",\"op_mark_icolor\":0,\"op_mark_iurl\":\"\",\"editor_icon\":\"\",\"editor_nickname\":\"\",\"op_info\":\"\",\"post_like_url\":\"\",\"post_dislike_url\":\"\",\"app_download_url\":\"\",\"app_download_desc\":\"\",\"download_type\":\"\",\"site_logo\":{\"id\":0,\"style\":2,\"desc\":\"北青网\",\"link\":\"\",\"img\":{\"url\":\"\",\"width\":0,\"height\":0,\"type\":\"\",\"preload\":0,\"daoliu_url\":\"\",\"daoliu_title\":\"\"}},\"enable_dislike\":true,\"is_drop_down_style\":false,\"dislike_infos\":[{\"type\":0,\"code\":1,\"msg\":\"重复推荐\",\"with_review\":0},{\"type\":0,\"code\":2,\"msg\":\"郭晶晶\",\"with_review\":0},{\"type\":0,\"code\":4,\"msg\":\"娱乐\",\"with_review\":0},{\"type\":0,\"code\":3,\"msg\":\"明星\",\"with_review\":0},{\"type\":0,\"code\":5,\"msg\":\"来源:北青网\",\"with_review\":0},{\"type\":1,\"code\":13,\"msg\":\"内容低俗\",\"with_review\":0},{\"type\":1,\"code\":16,\"msg\":\"标题夸张\",\"with_review\":0},{\"type\":1,\"code\":11,\"msg\":\"广告软文\",\"with_review\":0}],\"reco_desc\":\"\",\"news_poi_mark\":{},\"item_ext_bar\":{},\"source_name\":\"北青网\",\"origin_src_name\":\"北青网\",\"publish_time\":1489122360000,\"content_type\":0,\"daoliu_type\":0,\"original_url\":\"https://t.ynet.cn/uc/3595404.html?uc\",\"content_length\":653,\"cmt_cnt\":0,\"load_priority\":0,\"use_cache\":0,\"fresh_time\":0,\"article_like_cnt\":8,\"like_cnt\":0,\"dislike_cnt\":0,\"view_cnt\":0,\"share_cnt\":0,\"read_id\":\"4513890473681201716\",\"query_tags\":[{\"tag\":\"郭晶晶\",\"url\":\"\"},{\"tag\":\"嫁入豪门\",\"url\":\"\"}],\"video_tags\":[{\"real_tag\":\"郭晶晶\",\"style\":\"\",\"url\":\"\",\"alias_tag\":\"\"},{\"real_tag\":\"嫁入豪门\",\"style\":\"\",\"url\":\"\",\"alias_tag\":\"\"}],\"show_impression_url\":\"\",\"wm_author\":{},\"participant_cnt\":0,\"avg_score\":0,\"matched_tags\":[],\"character_cards\":[],\"hot_cmts\":[],\"cmt_url\":\"http://m.uczzd.cn/webview/xissAllComments?app=chelaile-iflow&aid=12857290119362313440&cid=100&zzd_from=chelaile-iflow&uc_param_str=dndsvebichfrntcpgipf&uc_biz_str=S:custom|C:comment|N:true\",\"cmt_enabled\":true,\"redirect_ch_id\":0,\"redirect_ch_name\":\"\",\"redirect_ch_img\":\"\",\"redirect_ch_desc\":\"\",\"zzd_url\":\"http://m.uczzd.cn/webview/news?app=chelaile-iflow&aid=12857290119362313440&cid=100&zzd_from=chelaile-iflow&uc_param_str=dndsfrvesvntnwpfgicp&recoid=&rd_type=reco&sp_gz=0\",\"is_wemedia\":false,\"share_url\":\"http://s4.uczzd.cn/webview/news?app=chelaile-iflow&aid=12857290119362313440&cid=100&zzd_from=chelaile-iflow&uc_param_str=dndsfrvesvntnwpfgicp&recoid=&rd_type=share&sp_gz=0&pagetype=share&btifl=100\",\"ad_content\":{},\"qiqu_info\":{},\"title_img_hyperlink\":{},\"content_ext_audios\":[],\"reco_reason_type\":-1,\"reco_reason\":\"\",\"reco_tag_desc\":\"\",\"video_immersive_mode\":false,\"daoliu_switch\":{},\"event_tag_info\":[]},{\"id\":\"10036523814839483935\",\"recoid\":\"\",\"title\":\"同样是豪门媳妇, 36岁郭晶晶与35岁徐子淇同框, 太尴尬了!\",\"subhead\":\"\",\"url\":\"http://m.uczzd.cn/webview/news?app=chelaile-iflow&aid=10036523814839483935&cid=100&zzd_from=chelaile-iflow&uc_param_str=dndsfrvesvntnwpfgicp&recoid=&rd_type=reco&sp_gz=0\",\"hyperlinks\":[],\"strategy\":255,\"politics\":0,\"summary\":\"\",\"content\":\"\",\"thumbnails\":[{\"url\":\"http://image.uczzd.cn/10842126006610364382.jpeg?id=0\",\"width\":480,\"height\":259,\"type\":\"jpeg\",\"preload\":0,\"daoliu_url\":\"\",\"daoliu_title\":\"\"},{\"url\":\"http://image.uczzd.cn/12777726559256277323.jpeg?id=0\",\"width\":480,\"height\":430,\"type\":\"jpeg\",\"preload\":0,\"daoliu_url\":\"\",\"daoliu_title\":\"\"},{\"url\":\"http://image.uczzd.cn/14947313438730788580.jpeg?id=0\",\"width\":480,\"height\":317,\"type\":\"jpeg\",\"preload\":0,\"daoliu_url\":\"\",\"daoliu_title\":\"\"},{\"url\":\"http://image.uczzd.cn/10673602971445912724.jpeg?id=0\",\"width\":480,\"height\":310,\"type\":\"jpeg\",\"preload\":0,\"daoliu_url\":\"\",\"daoliu_title\":\"\"}],\"images\":[{\"title\":\"\",\"url\":\"http://image.uczzd.cn/10842126006610364382.jpeg?id=0\",\"description\":\"\",\"index\":0,\"width\":480,\"height\":259,\"preload\":1,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"https://image.ynet.cn/2017/03/14/3c0b9caddf7b7a551b233c5400f441dc_480x-_60.jpeg\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/12777726559256277323.jpeg?id=0\",\"description\":\"\",\"index\":1,\"width\":480,\"height\":430,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/14947313438730788580.jpeg?id=0\",\"description\":\"\",\"index\":2,\"width\":480,\"height\":317,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/10673602971445912724.jpeg?id=0\",\"description\":\"\",\"index\":3,\"width\":480,\"height\":310,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/887642143160710129.jpeg?id=0\",\"description\":\"\",\"index\":4,\"width\":480,\"height\":272,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/18138333341048536199.jpeg?id=0\",\"description\":\"\",\"index\":5,\"width\":388,\"height\":511,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0}],\"album\":{\"intro\":\"\",\"enabled\":false,\"cover_image\":\"\",\"img_pg_type\":0},\"tags\":[\"徐子淇\",\"郭晶晶\"],\"audios\":[],\"category\":[\"娱乐\",\"明星\"],\"videos\":[],\"cid\":179223212,\"contentExtHyperlinks\":[],\"contentExtVotes\":[],\"subArticle\":false,\"title_icon\":\"\",\"grab_time\":1489437715000,\"clickable_url\":true,\"item_type\":0,\"style_type\":0,\"op_mark\":\"\",\"op_mark_icolor\":0,\"op_mark_iurl\":\"\",\"editor_icon\":\"\",\"editor_nickname\":\"\",\"op_info\":\"\",\"post_like_url\":\"\",\"post_dislike_url\":\"\",\"app_download_url\":\"\",\"app_download_desc\":\"\",\"download_type\":\"\",\"site_logo\":{\"id\":0,\"style\":2,\"desc\":\"北青网\",\"link\":\"\",\"img\":{\"url\":\"\",\"width\":0,\"height\":0,\"type\":\"\",\"preload\":0,\"daoliu_url\":\"\",\"daoliu_title\":\"\"}},\"enable_dislike\":true,\"is_drop_down_style\":false,\"dislike_infos\":[{\"type\":0,\"code\":1,\"msg\":\"重复推荐\",\"with_review\":0},{\"type\":0,\"code\":2,\"msg\":\"徐子淇\",\"with_review\":0},{\"type\":0,\"code\":4,\"msg\":\"娱乐\",\"with_review\":0},{\"type\":0,\"code\":3,\"msg\":\"明星\",\"with_review\":0},{\"type\":0,\"code\":5,\"msg\":\"来源:北青网\",\"with_review\":0},{\"type\":1,\"code\":16,\"msg\":\"标题夸张\",\"with_review\":0},{\"type\":1,\"code\":13,\"msg\":\"内容低俗\",\"with_review\":0},{\"type\":1,\"code\":11,\"msg\":\"广告软文\",\"with_review\":0}],\"reco_desc\":\"\",\"news_poi_mark\":{},\"item_ext_bar\":{},\"source_name\":\"北青网\",\"origin_src_name\":\"北青网\",\"publish_time\":1489436160000,\"content_type\":0,\"daoliu_type\":0,\"original_url\":\"https://t.ynet.cn/uc/3617806.html?uc\",\"content_length\":681,\"cmt_cnt\":0,\"load_priority\":0,\"use_cache\":0,\"fresh_time\":0,\"article_like_cnt\":0,\"like_cnt\":0,\"dislike_cnt\":0,\"view_cnt\":0,\"share_cnt\":0,\"read_id\":\"14453741570444996563\",\"query_tags\":[{\"tag\":\"徐子淇\",\"url\":\"\"},{\"tag\":\"郭晶晶\",\"url\":\"\"}],\"video_tags\":[{\"real_tag\":\"徐子淇\",\"style\":\"\",\"url\":\"\",\"alias_tag\":\"\"},{\"real_tag\":\"郭晶晶\",\"style\":\"\",\"url\":\"\",\"alias_tag\":\"\"}],\"show_impression_url\":\"\",\"wm_author\":{},\"participant_cnt\":0,\"avg_score\":0,\"matched_tags\":[],\"character_cards\":[],\"hot_cmts\":[],\"cmt_url\":\"http://m.uczzd.cn/webview/xissAllComments?app=chelaile-iflow&aid=10036523814839483935&cid=100&zzd_from=chelaile-iflow&uc_param_str=dndsvebichfrntcpgipf&uc_biz_str=S:custom|C:comment|N:true\",\"cmt_enabled\":true,\"redirect_ch_id\":0,\"redirect_ch_name\":\"\",\"redirect_ch_img\":\"\",\"redirect_ch_desc\":\"\",\"zzd_url\":\"http://m.uczzd.cn/webview/news?app=chelaile-iflow&aid=10036523814839483935&cid=100&zzd_from=chelaile-iflow&uc_param_str=dndsfrvesvntnwpfgicp&recoid=&rd_type=reco&sp_gz=0\",\"is_wemedia\":false,\"share_url\":\"http://s4.uczzd.cn/webview/news?app=chelaile-iflow&aid=10036523814839483935&cid=100&zzd_from=chelaile-iflow&uc_param_str=dndsfrvesvntnwpfgicp&recoid=&rd_type=share&sp_gz=0&pagetype=share&btifl=100\",\"ad_content\":{},\"qiqu_info\":{},\"title_img_hyperlink\":{},\"content_ext_audios\":[],\"reco_reason_type\":-1,\"reco_reason\":\"\",\"reco_tag_desc\":\"\",\"video_immersive_mode\":false,\"daoliu_switch\":{},\"event_tag_info\":[]},{\"id\":\"12274349121162662859\",\"recoid\":\"\",\"title\":\"同是豪门太太 徐子淇似王妃保镖开路 郭晶晶接地气路边吃饭\",\"subhead\":\"\",\"url\":\"http://m.uczzd.cn/webview/news?app=chelaile-iflow&aid=12274349121162662859&cid=100&zzd_from=chelaile-iflow&uc_param_str=dndsfrvesvntnwpfgicp&recoid=&rd_type=reco&sp_gz=0\",\"hyperlinks\":[],\"strategy\":255,\"politics\":0,\"summary\":\"\",\"content\":\"\",\"thumbnails\":[{\"url\":\"http://image.uczzd.cn/1066011599725230026.jpeg?id=0\",\"width\":600,\"height\":413,\"type\":\"jpeg\",\"preload\":0,\"daoliu_url\":\"\",\"daoliu_title\":\"\"}],\"images\":[{\"title\":\"\",\"url\":\"http://image.uczzd.cn/1066011599725230026.jpeg?id=0\",\"description\":\"\",\"index\":0,\"width\":600,\"height\":413,\"preload\":1,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"http://img.mianfeicha.com/ershouche/2017/0310/163100067488500048ac.jpg\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/11105850681418368494.jpeg?id=0\",\"description\":\"\",\"index\":1,\"width\":360,\"height\":300,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/13895667956457640838.jpeg?id=0\",\"description\":\"\",\"index\":2,\"width\":640,\"height\":400,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/991878109963365835.jpeg?id=0\",\"description\":\"\",\"index\":3,\"width\":590,\"height\":388,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/10045946412344075878.jpeg?id=0\",\"description\":\"\",\"index\":4,\"width\":595,\"height\":558,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/5864767206690027115.jpeg?id=0\",\"description\":\"\",\"index\":5,\"width\":571,\"height\":386,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/10220403329444944786.jpeg?id=0\",\"description\":\"\",\"index\":6,\"width\":331,\"height\":400,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/6821316880130762678.jpeg?id=0\",\"description\":\"\",\"index\":7,\"width\":640,\"height\":427,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/17003845269378021789.jpeg?id=0\",\"description\":\"\",\"index\":8,\"width\":640,\"height\":915,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/4804700033921435504.jpeg?id=0\",\"description\":\"\",\"index\":9,\"width\":450,\"height\":346,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/10103500557477873532.jpeg?id=0\",\"description\":\"\",\"index\":10,\"width\":427,\"height\":352,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/5057804398433221922.jpeg?id=0\",\"description\":\"\",\"index\":11,\"width\":470,\"height\":366,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/14260311349312414132.jpeg?id=0\",\"description\":\"\",\"index\":12,\"width\":450,\"height\":414,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/14520272266821480209.jpeg?id=0\",\"description\":\"\",\"index\":13,\"width\":566,\"height\":462,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/4797378799605889397.jpeg?id=0\",\"description\":\"\",\"index\":14,\"width\":533,\"height\":325,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/2286346297392499749.jpeg?id=0\",\"description\":\"\",\"index\":15,\"width\":450,\"height\":261,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/10882811784166805590.jpeg?id=0\",\"description\":\"\",\"index\":16,\"width\":516,\"height\":306,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/10251784796846049704.jpeg?id=0\",\"description\":\"\",\"index\":17,\"width\":640,\"height\":629,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/18303862583990448762.jpeg?id=0\",\"description\":\"\",\"index\":18,\"width\":490,\"height\":342,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/9895278159241944261.jpeg?id=0\",\"description\":\"\",\"index\":19,\"width\":640,\"height\":421,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/13938113518865973413.jpeg?id=0\",\"description\":\"\",\"index\":20,\"width\":640,\"height\":960,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/17265149334888733147.jpeg?id=0\",\"description\":\"\",\"index\":21,\"width\":530,\"height\":352,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/12686924478686589353.jpeg?id=0\",\"description\":\"\",\"index\":22,\"width\":600,\"height\":303,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/9781074100060693709.jpeg?id=0\",\"description\":\"\",\"index\":23,\"width\":640,\"height\":412,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/17537230068680716663.jpeg?id=0\",\"description\":\"\",\"index\":24,\"width\":631,\"height\":346,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/2239710853375086613.jpeg?id=0\",\"description\":\"\",\"index\":25,\"width\":428,\"height\":480,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/17384671173569315603.jpeg?id=0\",\"description\":\"\",\"index\":26,\"width\":640,\"height\":526,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/5283736931517760419.jpeg?id=0\",\"description\":\"\",\"index\":27,\"width\":500,\"height\":333,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/15869775539908164155.jpeg?id=0\",\"description\":\"\",\"index\":28,\"width\":640,\"height\":500,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/13602694428673859787.jpeg?id=0\",\"description\":\"\",\"index\":29,\"width\":486,\"height\":355,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/13868495372651804416.jpeg?id=0\",\"description\":\"\",\"index\":30,\"width\":580,\"height\":773,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0}],\"album\":{\"intro\":\"\",\"enabled\":false,\"cover_image\":\"\",\"img_pg_type\":0},\"tags\":[\"徐子淇\",\"郭晶晶\"],\"audios\":[],\"category\":[\"娱乐\",\"明星\"],\"videos\":[],\"cid\":179223212,\"contentExtHyperlinks\":[],\"contentExtVotes\":[],\"subArticle\":false,\"title_icon\":\"\",\"grab_time\":1489143837000,\"clickable_url\":true,\"item_type\":0,\"style_type\":0,\"op_mark\":\"\",\"op_mark_icolor\":0,\"op_mark_iurl\":\"\",\"editor_icon\":\"\",\"editor_nickname\":\"\",\"op_info\":\"\",\"post_like_url\":\"\",\"post_dislike_url\":\"\",\"app_download_url\":\"\",\"app_download_desc\":\"\",\"download_type\":\"\",\"site_logo\":{\"id\":0,\"style\":2,\"desc\":\"花边星闻网\",\"link\":\"\",\"img\":{\"url\":\"\",\"width\":0,\"height\":0,\"type\":\"\",\"preload\":0,\"daoliu_url\":\"\",\"daoliu_title\":\"\"}},\"enable_dislike\":true,\"is_drop_down_style\":false,\"dislike_infos\":[{\"type\":0,\"code\":1,\"msg\":\"重复推荐\",\"with_review\":0},{\"type\":0,\"code\":2,\"msg\":\"徐子淇\",\"with_review\":0},{\"type\":0,\"code\":4,\"msg\":\"娱乐\",\"with_review\":0},{\"type\":0,\"code\":3,\"msg\":\"明星\",\"with_review\":0},{\"type\":0,\"code\":5,\"msg\":\"来源:花边星闻网\",\"with_review\":0},{\"type\":1,\"code\":13,\"msg\":\"内容低俗\",\"with_review\":0},{\"type\":1,\"code\":16,\"msg\":\"标题夸张\",\"with_review\":0},{\"type\":1,\"code\":11,\"msg\":\"广告软文\",\"with_review\":0}],\"reco_desc\":\"\",\"news_poi_mark\":{},\"item_ext_bar\":{},\"source_name\":\"花边星闻网\",\"origin_src_name\":\"papaluosha\",\"publish_time\":1489136246000,\"content_type\":0,\"daoliu_type\":0,\"original_url\":\"http://www.huabian.com/hbtoutiao/20170310/160351.html\",\"content_length\":992,\"cmt_cnt\":0,\"load_priority\":0,\"use_cache\":0,\"fresh_time\":0,\"article_like_cnt\":18,\"like_cnt\":0,\"dislike_cnt\":0,\"view_cnt\":0,\"share_cnt\":0,\"read_id\":\"7003212686400107269\",\"query_tags\":[{\"tag\":\"徐子淇\",\"url\":\"\"},{\"tag\":\"郭晶晶\",\"url\":\"\"}],\"video_tags\":[{\"real_tag\":\"徐子淇\",\"style\":\"\",\"url\":\"\",\"alias_tag\":\"\"},{\"real_tag\":\"郭晶晶\",\"style\":\"\",\"url\":\"\",\"alias_tag\":\"\"}],\"show_impression_url\":\"\",\"wm_author\":{},\"participant_cnt\":0,\"avg_score\":0,\"matched_tags\":[],\"character_cards\":[],\"hot_cmts\":[],\"cmt_url\":\"http://m.uczzd.cn/webview/xissAllComments?app=chelaile-iflow&aid=12274349121162662859&cid=100&zzd_from=chelaile-iflow&uc_param_str=dndsvebichfrntcpgipf&uc_biz_str=S:custom|C:comment|N:true\",\"cmt_enabled\":true,\"redirect_ch_id\":0,\"redirect_ch_name\":\"\",\"redirect_ch_img\":\"\",\"redirect_ch_desc\":\"\",\"zzd_url\":\"http://m.uczzd.cn/webview/news?app=chelaile-iflow&aid=12274349121162662859&cid=100&zzd_from=chelaile-iflow&uc_param_str=dndsfrvesvntnwpfgicp&recoid=&rd_type=reco&sp_gz=0\",\"is_wemedia\":false,\"share_url\":\"http://s4.uczzd.cn/webview/news?app=chelaile-iflow&aid=12274349121162662859&cid=100&zzd_from=chelaile-iflow&uc_param_str=dndsfrvesvntnwpfgicp&recoid=&rd_type=share&sp_gz=0&pagetype=share&btifl=100\",\"ad_content\":{},\"qiqu_info\":{},\"title_img_hyperlink\":{},\"content_ext_audios\":[],\"reco_reason_type\":-1,\"reco_reason\":\"\",\"reco_tag_desc\":\"\",\"video_immersive_mode\":false,\"daoliu_switch\":{},\"event_tag_info\":[]},{\"id\":\"24767543933971171\",\"recoid\":\"\",\"title\":\"赵薇郭晶晶等10大豪门阔太谁过得最好?\",\"subhead\":\"\",\"url\":\"http://m.uczzd.cn/webview/news?app=chelaile-iflow&aid=24767543933971171&cid=100&zzd_from=chelaile-iflow&uc_param_str=dndsfrvesvntnwpfgicp&recoid=&rd_type=reco&sp_gz=0\",\"hyperlinks\":[],\"strategy\":255,\"politics\":0,\"summary\":\"\",\"content\":\"\",\"thumbnails\":[{\"url\":\"http://image.uczzd.cn/12154594425245971280.jpeg?id=0\",\"width\":548,\"height\":431,\"type\":\"jpeg\",\"preload\":0,\"daoliu_url\":\"\",\"daoliu_title\":\"\"},{\"url\":\"http://image.uczzd.cn/17203393301403998398.jpeg?id=0\",\"width\":548,\"height\":389,\"type\":\"jpeg\",\"preload\":0,\"daoliu_url\":\"\",\"daoliu_title\":\"\"},{\"url\":\"http://image.uczzd.cn/2203558286086367229.jpeg?id=0\",\"width\":471,\"height\":410,\"type\":\"jpeg\",\"preload\":0,\"daoliu_url\":\"\",\"daoliu_title\":\"\"},{\"url\":\"http://image.uczzd.cn/14440740088138560745.jpeg?id=0\",\"width\":430,\"height\":432,\"type\":\"jpeg\",\"preload\":0,\"daoliu_url\":\"\",\"daoliu_title\":\"\"}],\"images\":[{\"title\":\"\",\"url\":\"http://image.uczzd.cn/12154594425245971280.jpeg?id=0\",\"description\":\"\",\"index\":0,\"width\":548,\"height\":431,\"preload\":1,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"http://images.jinghua.cn/titiPlepic/PiPlepic/2017/03/10/18/0FpFSIGAi7.jpeg\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/17203393301403998398.jpeg?id=0\",\"description\":\"\",\"index\":1,\"width\":548,\"height\":389,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/2203558286086367229.jpeg?id=0\",\"description\":\"\",\"index\":2,\"width\":471,\"height\":410,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/14440740088138560745.jpeg?id=0\",\"description\":\"\",\"index\":3,\"width\":430,\"height\":432,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/2417700095612679491.jpeg?id=0\",\"description\":\"\",\"index\":4,\"width\":462,\"height\":390,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/761285556214090047.jpeg?id=0\",\"description\":\"\",\"index\":5,\"width\":474,\"height\":498,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/11102853652050303318.jpeg?id=0\",\"description\":\"\",\"index\":6,\"width\":457,\"height\":626,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/7520876682086585770.jpeg?id=0\",\"description\":\"\",\"index\":7,\"width\":474,\"height\":468,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/11744412701484221123.jpeg?id=0\",\"description\":\"\",\"index\":8,\"width\":468,\"height\":328,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/3701612626438170093.jpeg?id=0\",\"description\":\"\",\"index\":9,\"width\":473,\"height\":294,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0}],\"album\":{\"intro\":\"\",\"enabled\":false,\"cover_image\":\"\",\"img_pg_type\":0},\"tags\":[\"郭晶晶\",\"赵薇\",\"刘涛\",\"霍启刚\"],\"audios\":[],\"category\":[\"娱乐\",\"明星\"],\"videos\":[],\"cid\":179223212,\"contentExtHyperlinks\":[],\"contentExtVotes\":[],\"subArticle\":false,\"title_icon\":\"\",\"grab_time\":1489141747000,\"clickable_url\":true,\"item_type\":0,\"style_type\":0,\"op_mark\":\"\",\"op_mark_icolor\":0,\"op_mark_iurl\":\"\",\"editor_icon\":\"\",\"editor_nickname\":\"\",\"op_info\":\"\",\"post_like_url\":\"\",\"post_dislike_url\":\"\",\"app_download_url\":\"\",\"app_download_desc\":\"\",\"download_type\":\"\",\"site_logo\":{\"id\":0,\"style\":2,\"desc\":\"京华网\",\"link\":\"\",\"img\":{\"url\":\"\",\"width\":0,\"height\":0,\"type\":\"\",\"preload\":0,\"daoliu_url\":\"\",\"daoliu_title\":\"\"}},\"enable_dislike\":true,\"is_drop_down_style\":false,\"dislike_infos\":[{\"type\":0,\"code\":1,\"msg\":\"重复推荐\",\"with_review\":0},{\"type\":0,\"code\":2,\"msg\":\"郭晶晶\",\"with_review\":0},{\"type\":0,\"code\":4,\"msg\":\"娱乐\",\"with_review\":0},{\"type\":0,\"code\":3,\"msg\":\"明星\",\"with_review\":0},{\"type\":0,\"code\":5,\"msg\":\"来源:京华网\",\"with_review\":0},{\"type\":1,\"code\":13,\"msg\":\"内容低俗\",\"with_review\":0},{\"type\":1,\"code\":16,\"msg\":\"标题夸张\",\"with_review\":0},{\"type\":1,\"code\":11,\"msg\":\"广告软文\",\"with_review\":0}],\"reco_desc\":\"\",\"news_poi_mark\":{},\"item_ext_bar\":{},\"source_name\":\"京华网\",\"origin_src_name\":\"京华网\",\"publish_time\":1489141020000,\"content_type\":0,\"daoliu_type\":0,\"original_url\":\"http://uc.jinghua.cn/20170310/244044.html?s=cm\",\"content_length\":2121,\"cmt_cnt\":1,\"load_priority\":0,\"use_cache\":0,\"fresh_time\":0,\"article_like_cnt\":6,\"like_cnt\":0,\"dislike_cnt\":0,\"view_cnt\":0,\"share_cnt\":0,\"read_id\":\"7936551008201362432\",\"query_tags\":[{\"tag\":\"郭晶晶\",\"url\":\"\"},{\"tag\":\"赵薇\",\"url\":\"\"},{\"tag\":\"刘涛\",\"url\":\"\"},{\"tag\":\"霍启刚\",\"url\":\"\"}],\"video_tags\":[{\"real_tag\":\"郭晶晶\",\"style\":\"\",\"url\":\"\",\"alias_tag\":\"\"},{\"real_tag\":\"赵薇\",\"style\":\"\",\"url\":\"\",\"alias_tag\":\"\"},{\"real_tag\":\"刘涛\",\"style\":\"\",\"url\":\"\",\"alias_tag\":\"\"},{\"real_tag\":\"霍启刚\",\"style\":\"\",\"url\":\"\",\"alias_tag\":\"\"}],\"show_impression_url\":\"\",\"wm_author\":{},\"participant_cnt\":0,\"avg_score\":0,\"matched_tags\":[],\"character_cards\":[],\"hot_cmts\":[],\"cmt_url\":\"http://m.uczzd.cn/webview/xissAllComments?app=chelaile-iflow&aid=24767543933971171&cid=100&zzd_from=chelaile-iflow&uc_param_str=dndsvebichfrntcpgipf&uc_biz_str=S:custom|C:comment|N:true\",\"cmt_enabled\":true,\"redirect_ch_id\":0,\"redirect_ch_name\":\"\",\"redirect_ch_img\":\"\",\"redirect_ch_desc\":\"\",\"zzd_url\":\"http://m.uczzd.cn/webview/news?app=chelaile-iflow&aid=24767543933971171&cid=100&zzd_from=chelaile-iflow&uc_param_str=dndsfrvesvntnwpfgicp&recoid=&rd_type=reco&sp_gz=0\",\"is_wemedia\":false,\"share_url\":\"http://s4.uczzd.cn/webview/news?app=chelaile-iflow&aid=24767543933971171&cid=100&zzd_from=chelaile-iflow&uc_param_str=dndsfrvesvntnwpfgicp&recoid=&rd_type=share&sp_gz=0&pagetype=share&btifl=100\",\"ad_content\":{},\"qiqu_info\":{},\"title_img_hyperlink\":{},\"content_ext_audios\":[],\"reco_reason_type\":-1,\"reco_reason\":\"\",\"reco_tag_desc\":\"\",\"video_immersive_mode\":false,\"daoliu_switch\":{},\"event_tag_info\":[]},{\"id\":\"7500519613554910028\",\"recoid\":\"\",\"title\":\"郭晶晶刘涛上榜 女星令人称奇的“一见钟情”\",\"subhead\":\"\",\"url\":\"http://m.uczzd.cn/webview/news?app=chelaile-iflow&aid=7500519613554910028&cid=100&zzd_from=chelaile-iflow&uc_param_str=dndsfrvesvntnwpfgicp&recoid=&rd_type=reco&sp_gz=0\",\"hyperlinks\":[],\"strategy\":255,\"politics\":0,\"summary\":\"\",\"content\":\"\",\"thumbnails\":[{\"url\":\"http://image.uczzd.cn/14373752041777253031.jpeg?id=0\",\"width\":379,\"height\":500,\"type\":\"jpeg\",\"preload\":0,\"daoliu_url\":\"\",\"daoliu_title\":\"\"}],\"images\":[{\"title\":\"\",\"url\":\"http://image.uczzd.cn/14373752041777253031.jpeg?id=0\",\"description\":\"\",\"index\":0,\"width\":379,\"height\":500,\"preload\":1,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"https://image.ynet.cn/2017/03/08/0505a7c25d944f5f6f6bf3e2aa7ce59a_480x-_60.jpg\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/10858759541114096910.jpeg?id=0\",\"description\":\"\",\"index\":1,\"width\":423,\"height\":500,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/8630716925324750843.jpeg?id=0\",\"description\":\"\",\"index\":2,\"width\":428,\"height\":500,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/11882932738329776888.jpeg?id=0\",\"description\":\"\",\"index\":3,\"width\":383,\"height\":500,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/12768845677328903939.jpeg?id=0\",\"description\":\"\",\"index\":4,\"width\":405,\"height\":500,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0},{\"title\":\"\",\"url\":\"http://image.uczzd.cn/10130427812479302177.jpeg?id=0\",\"description\":\"\",\"index\":5,\"width\":400,\"height\":500,\"preload\":0,\"type\":\"jpeg\",\"focus\":\"50_50\",\"original_url\":\"\",\"daoliu_url\":\"\",\"daoliu_title\":\"\",\"gallery_id\":\"\",\"gallery_type\":0,\"is_hd\":0}],\"album\":{\"intro\":\"\",\"enabled\":false,\"cover_image\":\"\",\"img_pg_type\":0},\"tags\":[\"郭晶晶\",\"刘涛\"],\"audios\":[],\"category\":[\"娱乐\",\"明星\"],\"videos\":[],\"cid\":200,\"contentExtHyperlinks\":[],\"contentExtVotes\":[],\"subArticle\":false,\"title_icon\":\"\",\"grab_time\":1488979917000,\"clickable_url\":true,\"item_type\":0,\"style_type\":0,\"op_mark\":\"\",\"op_mark_icolor\":0,\"op_mark_iurl\":\"\",\"editor_icon\":\"\",\"editor_nickname\":\"\",\"op_info\":\"\",\"post_like_url\":\"\",\"post_dislike_url\":\"\",\"app_download_url\":\"\",\"app_download_desc\":\"\",\"download_type\":\"\",\"site_logo\":{\"id\":0,\"style\":2,\"desc\":\"北青网\",\"link\":\"\",\"img\":{\"url\":\"\",\"width\":0,\"height\":0,\"type\":\"\",\"preload\":0,\"daoliu_url\":\"\",\"daoliu_title\":\"\"}},\"enable_dislike\":true,\"is_drop_down_style\":false,\"dislike_infos\":[{\"type\":0,\"code\":1,\"msg\":\"重复推荐\",\"with_review\":0},{\"type\":0,\"code\":2,\"msg\":\"郭晶晶\",\"with_review\":0},{\"type\":0,\"code\":4,\"msg\":\"娱乐\",\"with_review\":0},{\"type\":0,\"code\":3,\"msg\":\"明星\",\"with_review\":0},{\"type\":0,\"code\":5,\"msg\":\"来源:北青网\",\"with_review\":0},{\"type\":1,\"code\":16,\"msg\":\"标题夸张\",\"with_review\":0},{\"type\":1,\"code\":13,\"msg\":\"内容低俗\",\"with_review\":0},{\"type\":1,\"code\":11,\"msg\":\"广告软文\",\"with_review\":0}],\"reco_desc\":\"\",\"news_poi_mark\":{},\"item_ext_bar\":{},\"source_name\":\"北青网\",\"origin_src_name\":\"北青网\",\"publish_time\":1488978900000,\"content_type\":0,\"daoliu_type\":0,\"original_url\":\"https://t.ynet.cn/uc/3582282.html?uc\",\"content_length\":502,\"cmt_cnt\":1,\"load_priority\":0,\"use_cache\":0,\"fresh_time\":0,\"article_like_cnt\":0,\"like_cnt\":0,\"dislike_cnt\":0,\"view_cnt\":0,\"share_cnt\":0,\"read_id\":\"11632744346749873951\",\"query_tags\":[{\"tag\":\"郭晶晶\",\"url\":\"\"},{\"tag\":\"刘涛\",\"url\":\"\"}],\"video_tags\":[{\"real_tag\":\"郭晶晶\",\"style\":\"\",\"url\":\"\",\"alias_tag\":\"\"},{\"real_tag\":\"刘涛\",\"style\":\"\",\"url\":\"\",\"alias_tag\":\"\"}],\"show_impression_url\":\"\",\"wm_author\":{},\"participant_cnt\":0,\"avg_score\":0,\"matched_tags\":[],\"character_cards\":[],\"hot_cmts\":[],\"cmt_url\":\"http://m.uczzd.cn/webview/xissAllComments?app=chelaile-iflow&aid=7500519613554910028&cid=100&zzd_from=chelaile-iflow&uc_param_str=dndsvebichfrntcpgipf&uc_biz_str=S:custom|C:comment|N:true\",\"cmt_enabled\":true,\"redirect_ch_id\":0,\"redirect_ch_name\":\"\",\"redirect_ch_img\":\"\",\"redirect_ch_desc\":\"\",\"zzd_url\":\"http://m.uczzd.cn/webview/news?app=chelaile-iflow&aid=7500519613554910028&cid=100&zzd_from=chelaile-iflow&uc_param_str=dndsfrvesvntnwpfgicp&recoid=&rd_type=reco&sp_gz=0\",\"is_wemedia\":false,\"share_url\":\"http://s4.uczzd.cn/webview/news?app=chelaile-iflow&aid=7500519613554910028&cid=100&zzd_from=chelaile-iflow&uc_param_str=dndsfrvesvntnwpfgicp&recoid=&rd_type=share&sp_gz=0&pagetype=share&btifl=100\",\"ad_content\":{},\"qiqu_info\":{},\"title_img_hyperlink\":{},\"content_ext_audios\":[],\"reco_reason_type\":-1,\"reco_reason\":\"\",\"reco_tag_desc\":\"\",\"video_immersive_mode\":false,\"daoliu_switch\":{},\"event_tag_info\":[]}]},\"status\":0,\"message\":\"ok\",\"result\":{\"status\":0,\"message\":\"ok\"}}";
		// AdvParam advParam = new AdvParam();
		//
		// ArticleRecomResponse articleRecomResponse =
		// JSON.parseObject(response, ArticleRecomResponse.class);
		// if(articleRecomResponse == null ||
		// articleRecomResponse.getData().getStatus() != 0
		// || articleRecomResponse.getData() == null) {
		// logger.error("获取相关推荐出错,response={}", response);
		// return ;
		// }
		//
		// List<ArticleResponseData> articleRecomList =
		// articleRecomResponse.getData().getArticles();
		// List<ArticleInfo> articles = new ArrayList<ArticleInfo>();
		// for(ArticleResponseData ar : articleRecomList) {
		// ArticleInfo articleInfo = new ArticleInfo();
		// articleInfo.setTitle(ar.getTitle());
		// articleInfo.setDesc(DateUtil.flowFormatTime(ar.getGrab_time() + ""));
		// articleInfo.setAuthor(ar.getOrigin_src_name());
		// articleInfo.setUrl(ar.handleUrl(advParam));
		// articleInfo.setImgUrl(ar.handleImgUrls());
		//
		// if(articleInfo.getImgUrl() == null) {
		// logger.error("解析推荐文章出错，图片为空: response={}", response);
		// continue;
		// }
		// articles.add(articleInfo);
		// }

		// String response =
		// "{\"data\":{\"id\":\"16475077319356747800\",\"recoid\":\"\",\"title\":\"油价降了！明起，加一箱油省3块钱\",\"subhead\":\"\",\"url\":\"http://open.uczzd.cn/webview/news?app=chelaile-iflow&aid=16475077319356747800&cid=0&zzd_from=chelaile-iflow&uc_param_str=dndsfrvesvntnwpfgicp&recoid=&rd_type=reco&sp_gz=0\",\"hyperlinks\":[],\"strategy\":255,\"politics\":0,\"summary\":\"\",\"content\":\"<p>（央视财经记者 平凡 张婷敏）3月14日24时，2017年第5次成品油调价窗口将再次开启。本轮调价周期内，国际市场原油价格大幅下降，受此影响，国内成品油价格也作出相应下调。记者刚刚从国家发改委获悉：汽油、柴油每吨均将下调85元。</p><p>国内油价小幅下调，明起，加一箱油省三块</p><p>本次油价调整是年内第二次油价下调，从全国平均来看，国内成品油的价格浮动具体情况如下：90号汽油每升下调0.06元，92号汽油每升下调0.06元，95号汽油每升下调0.07元，0号柴油每升下调0.07元。按一般家用汽车油箱50L容量估测，加满一箱92号汽油将节省约3元。</p><p>国际油价“跌跌不休”，国内油价应声下调</p><p>据国家发展改革委价格监测中心监测，本轮成品油调价周期（3月1日-3月14日），受美国原油库存不断升高、页岩油增产和美元走强等因素影响，国际油价大幅回落，布伦特、WTI原油期货平均价格比上轮调价周期下降2.84%。</p><p>附表：国际原油期货收盘价格（3月1日-3月14日）</p><p>根据记者从发改委获得的数据，截至3月13日，两市油价分别为每桶51.35美元、48.40美元，均回落至2016年11月末欧佩克达成限产协议前的水平。</p><p>目前，油价降至每桶50美元支撑位附近，全球炼厂将迎来检修期和成品油需求淡季，将导致原油需求降低，预计短期供应过剩局面影响油价处于相对低位。</p><p>国内成品油价小幅下调 多地油价仍逼近7元关口</p><p>国内成品油定价机制采用 “十个工作日一调”的原则，截至本次调整，2017年成品油调价已完成2次上调、2次下调，1次搁浅。此次调整后，以93号汽油为例，仍有约20个省份油价维持在6.5元/升以上，不少省份逼近“7元”大关，如内蒙古、云南等。</p><p>以下为部分地区油价概况：</p><p>① 北京92#号汽油将从6.53元/升，降至6.47元/升；95#号汽油将从6.95元/升，降至6.88元/升；</p><p>② 上海92#号汽油将从6.49元/升，降至6.43元/升，95#号汽油将从6.91元/升，降至6.84元/升；</p><p>③广东93#号汽油将从6.55元/升，降至6.49元/升，97#号汽油将从7.10元/升，降至7.03元/升</p><p>④贵州90#汽油将从6.98元/升，降至6.92元/升；</p><p>⑤黑龙江90#汽油将从5.87元/升，降至5.81元/升；</p>\",\"thumbnails\":[{\"url\":\"http://image.uczzd.cn/5272472505200279354.?id=0\",\"width\":197,\"height\":147,\"type\":\"\",\"preload\":0,\"daoliu_url\":\"\",\"daoliu_title\":\"\"}],\"images\":[],\"album\":{\"intro\":\"\",\"enabled\":false,\"cover_image\":\"\",\"img_pg_type\":0},\"tags\":[\"油价\",\"汽油\",\"成品油\",\"原油\"],\"audios\":[],\"category\":[\"财经\",\"期货\"],\"videos\":[],\"cid\":26325229,\"contentExtHyperlinks\":[],\"contentExtVotes\":[],\"subArticle\":false,\"title_icon\":\"\",\"grab_time\":1489472738000,\"clickable_url\":true,\"item_type\":0,\"style_type\":1,\"op_mark\":\"热点\",\"op_mark_icolor\":15878718,\"op_mark_iurl\":\"http://image.uczzd.cn/12013266730753243483.jpg?id=0;#f24a3e\",\"editor_icon\":\"\",\"editor_nickname\":\"\",\"op_info\":\"push_dim:全量;title_color:0;op_type:全量;item_class:财经`期货;\",\"post_like_url\":\"\",\"post_dislike_url\":\"\",\"app_download_url\":\"\",\"app_download_desc\":\"\",\"download_type\":\"\",\"site_logo\":{\"id\":0,\"style\":2,\"desc\":\"UC头条\",\"link\":\"\",\"img\":{\"url\":\"\",\"width\":0,\"height\":0,\"type\":\"\",\"preload\":0,\"daoliu_url\":\"\",\"daoliu_title\":\"\"}},\"enable_dislike\":true,\"is_drop_down_style\":false,\"dislike_infos\":[{\"type\":0,\"code\":1,\"msg\":\"重复推荐\",\"with_review\":0},{\"type\":0,\"code\":2,\"msg\":\"油价\",\"with_review\":0},{\"type\":0,\"code\":4,\"msg\":\"财经\",\"with_review\":0},{\"type\":0,\"code\":3,\"msg\":\"期货\",\"with_review\":0},{\"type\":0,\"code\":5,\"msg\":\"来源:UC头条\",\"with_review\":0},{\"type\":1,\"code\":16,\"msg\":\"标题夸张\",\"with_review\":0},{\"type\":1,\"code\":13,\"msg\":\"内容低俗\",\"with_review\":0},{\"type\":1,\"code\":11,\"msg\":\"广告软文\",\"with_review\":0}],\"reco_desc\":\"\",\"news_poi_mark\":{},\"item_ext_bar\":{},\"source_name\":\"UC头条\",\"origin_src_name\":\"UC头条\",\"publish_time\":1489481651000,\"content_type\":0,\"daoliu_type\":0,\"original_url\":\"http://m.uczzd.cn/webapp/webview/article/news.html?app=uc-iflow&aid=16475077319356747800&cid=100&zzd_from=uc-iflow&uc_param_str=dndseiwifrvesvntgipf&recoid=&readId=&rd_type=reco&previewdl=1\",\"content_length\":1048,\"cmt_cnt\":0,\"load_priority\":0,\"use_cache\":0,\"fresh_time\":0,\"article_like_cnt\":8,\"like_cnt\":0,\"dislike_cnt\":0,\"view_cnt\":0,\"share_cnt\":0,\"read_id\":\"18004569108680658022\",\"query_tags\":[{\"tag\":\"油价\",\"url\":\"\"},{\"tag\":\"汽油\",\"url\":\"\"},{\"tag\":\"成品油\",\"url\":\"\"},{\"tag\":\"原油\",\"url\":\"\"}],\"video_tags\":[{\"real_tag\":\"油价\",\"style\":\"\",\"url\":\"\",\"alias_tag\":\"\"},{\"real_tag\":\"汽油\",\"style\":\"\",\"url\":\"\",\"alias_tag\":\"\"},{\"real_tag\":\"成品油\",\"style\":\"\",\"url\":\"\",\"alias_tag\":\"\"},{\"real_tag\":\"原油\",\"style\":\"\",\"url\":\"\",\"alias_tag\":\"\"}],\"show_impression_url\":\"\",\"wm_author\":{},\"participant_cnt\":0,\"avg_score\":0,\"matched_tags\":[],\"character_cards\":[],\"hot_cmts\":[],\"cmt_url\":\"http://open.uczzd.cn/webview/xissAllComments?app=chelaile-iflow&aid=16475077319356747800&cid=0&zzd_from=chelaile-iflow&uc_param_str=dndsvebichfrntcpgipf&uc_biz_str=S:custom|C:comment|N:true\",\"cmt_enabled\":true,\"redirect_ch_id\":0,\"redirect_ch_name\":\"\",\"redirect_ch_img\":\"\",\"redirect_ch_desc\":\"\",\"zzd_url\":\"http://open.uczzd.cn/webview/news?app=chelaile-iflow&aid=16475077319356747800&cid=0&zzd_from=chelaile-iflow&uc_param_str=dndsfrvesvntnwpfgicp&recoid=&rd_type=reco&sp_gz=0\",\"is_wemedia\":false,\"share_url\":\"http://s4.uczzd.cn/webview/news?app=chelaile-iflow&aid=16475077319356747800&cid=0&zzd_from=chelaile-iflow&uc_param_str=dndsfrvesvntnwpfgicp&recoid=&rd_type=share&sp_gz=0&pagetype=share&btifl=100\",\"ad_content\":{},\"qiqu_info\":{},\"title_img_hyperlink\":{},\"content_ext_audios\":[],\"reco_reason_type\":-1,\"reco_reason\":\"\",\"reco_tag_desc\":\"\",\"video_immersive_mode\":false,\"daoliu_switch\":{\"icon\":false,\"relate\":false,\"comment\":false,\"img_desc\":false,\"hot_query\":false},\"event_tag_info\":[]},\"status\":0,\"message\":\"ok\",\"result\":{\"status\":0,\"message\":\"ok\"}}";
		// ArticleResponse articleRes = JSON.parseObject(response,
		// ArticleResponse.class);
		// if(articleRes.getStatus() != 0 || articleRes.getData() == null
		// || articleRes.getData().getContent() == null
		// || articleRes.getData().getImages() == null) {
		// logger.error("获取文章出错,response={}", response);
		// }
		//
		//
		// ArticleInfo articleInfo = new ArticleInfo();
		// articleInfo.setTitle(articleRes.getData().getTitle());
		// try {
		// articleInfo.setDesc(DateUtil.getFormatTime(new
		// Date(articleRes.getData().getGrab_time()), "MM-dd"));
		// } catch (ParseException e) {
		// logger.error("时间转换出错,response={}", response);
		// e.printStackTrace();
		// }
		// articleInfo.setAuthor(articleRes.getData().getOrigin_src_name());
		// articleInfo.setContent(articleRes.getData().handleContent());
		// articleInfo.setImgRtio(articleRes.getData().handleImgRtio());
		// articleInfo.setShareDesc(articleRes.getData().handlShareDesc());

		String token = "1489428602505-ece29b313328581a74ff847d8cfc6205-38d8ce53c6051e87bf0136cdf586cba2";
		int channelId = 100;
		long ftime = -1;
		String recoid = "4631130733898610858";
		AdvParam advParam = new AdvParam();
		advParam.setUdid("22222");

		String url = String.format(requestUrl, channelId, "new", ftime, recoid, advParam.getS(), token,
				advParam.getUdid());
		System.out.println(url);

	}

}
