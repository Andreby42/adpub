package com.bus.chelaile.mvc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.common.AdvCache;
import com.bus.chelaile.common.AnalysisLog;
import com.bus.chelaile.common.CacheUtil;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.flow.ActivityService;
import com.bus.chelaile.flow.FlowService;
import com.bus.chelaile.flow.XishuashuaHelp;
import com.bus.chelaile.model.QueueCacheType;
import com.bus.chelaile.service.ServiceManager;
import com.bus.chelaile.service.SynchronizationControl;
import com.bus.chelaile.thread.Queue;
import com.bus.chelaile.thread.model.QueueObject;
import com.bus.chelaile.flow.model.ArticleInfo;
import com.bus.chelaile.flow.model.FlowResponse;
import com.bus.chelaile.flow.model.ListIdsCache;
import com.bus.chelaile.flow.model.TabEntity;

/**
 * 广告相关接口
 * 
 * @author zzz
 * 
 */

@Controller
@RequestMapping("")
public class BusUCArticlesAction extends AbstractController {

	@Resource
	private ServiceManager serviceManager;
	@Resource
	private FlowService flowService;
	@Resource
	private ActivityService activityService;
	@Resource
	private XishuashuaHelp xishuashuaHelp;

	private static final Logger logger = LoggerFactory.getLogger(BusUCArticlesAction.class);

	/*
	 * 洗刷刷 uc 文章获取 v 1.0 ,仅供h5使用 v 2.0 ,供app客户端使用
	 * 2018/03/05记：，仅供‘福利社’下方信息流使用
	 */
	@ResponseBody
	@RequestMapping(value = "adv!getUCArticles.action", produces = "text/plain;charset=UTF-8")
	public String getUCArticles(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		AdvParam advParam = getActionParam(request);
		advParam.setPicHeight(request.getParameter("picHeight"));
		advParam.setPicWidth(request.getParameter("picWidth"));
		advParam.setRefer(request.getParameter("stats_referer")); // 指明是‘详情页’的信息流，还是‘发现’信息流
		// X-Forwarded-For 设置
		// 直接来自h5的请求，取x_forwarded_for作为ip
		String ip = request.getParameter("x_forwarded_for");
		if(StringUtils.isNoneBlank(ip))
			advParam.setIp(ip);
		
		long ftime = getLong(request, "ftime");
		String recoid = request.getParameter("recoid");
		int id = getInt(request, "channelId");
		//是否支持小说。如果支持小说，那么样式是固定单栏，并且投放不受udid限制。
		int supportNovel = getInt(request, "supportNovel");	
		
		FlowResponse flowResponse = flowService.getResponse(advParam, ftime, recoid, id, supportNovel);
		if (flowResponse == null) {
			return serviceManager.getClientErrMap("返回为空", Constants.STATUS_INTERNAL_ERROR);
		}
//		if (flowResponse.getUcArticles() == null  || flowResponse.getUcArticles().size() == 0) {
//			return serviceManager.getClienSucMap(flowResponse, Constants.STATUS_INTERNAL_ERROR);	// 没有文章内容
//		}
		return serviceManager.getClienSucMap(flowResponse, Constants.STATUS_REQUEST_SUCCESS);
	}

	/*
	 * uc 信息流 不感兴趣
	 */
	@ResponseBody
	@RequestMapping(value = "adv!uninterestUC.action", produces = "text/plain;charset=UTF-8")
	public String uninterestUC(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		AdvParam advParam = getActionParam(request);

		String articleId = request.getParameter("articleId");
		int articleType = getInt(request, "articleType");

		flowService.uninterest(advParam, articleId, articleType);

		return serviceManager.getClienSucMap(new JSONObject(), Constants.STATUS_REQUEST_SUCCESS);
	}

	/*
	 * reloadUCArticles
	 */
	@ResponseBody
	@RequestMapping(value = "adv!reloadUCArticles.action", produces = "text/plain;charset=UTF-8")
	public String reloadUCArticles(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws Exception {
		logger.info("reload UCArticles ***********************************");

		long start = System.currentTimeMillis();
		if (!SynchronizationControl.isReloadUC()) {
			try {
				SynchronizationControl.setReloadUCLockState(true);
				activityService.initActivitity();
				logger.info("reload UCArticles costs {} ms", (System.currentTimeMillis() - start));
				return serviceManager.getClienSucMap(new JSONObject(), Constants.STATUS_REQUEST_SUCCESS);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				logger.error("reloadUCArticles exception");
			} finally {
				SynchronizationControl.setReloadUCLockState(false);
			}
		} else {
			logger.info("reloadDatas repeat failed");
			return serviceManager.getClientErrMap("请稍后再试", "01");
		}

		return serviceManager.getClienSucMap(new JSONObject(), Constants.STATUS_REQUEST_SUCCESS);
	}

	/*
	 * uc 信息流 点击跳转我们自己写的网页
	 */
	@ResponseBody
	@RequestMapping(value = "adv!articlesPage.action", produces = "text/plain;charset=UTF-8")
	public void articlesPage(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		try {
			AdvParam advParam = getActionParam(request);

			String articleId = request.getParameter("articleId");
			if (StringUtils.isEmpty(articleId)) {
				logger.error("请求文章详情出错: articleId={}", articleId);
			}

			ArticleInfo articleInfo = xishuashuaHelp.getArticleInfo(articleId);
			if (articleInfo == null) {
				RequestDispatcher rd = request.getRequestDispatcher("404.jsp");
				rd.forward(request, response);
			}

			List<ArticleInfo> recomArticles = xishuashuaHelp.getArticleRecom(articleId, advParam);
			if (recomArticles == null || recomArticles.size() == 0) {
				logger.error("获取推荐文章失败,原文章id={}", articleId);
			}

			request.setAttribute("articleInfo", articleInfo);
			request.setAttribute("recomArticles", recomArticles);
			AnalysisLog
					.info("【AritilceClick】 udid:{} |# s:{} |# v:{} |# city_id:{} |# article_id:{} |# article_title:{} "
							+ "|# line_id:{} |# account_id:{}  |# nw:{} |# ip:{} |# diviceType:{} |# stats_act:{} |# geo_lat:{} "
							+ "|# geo_lng:{} |# linkReffer:{} ", advParam.getUdid(), advParam.getS(), advParam.getV(),
							advParam.getCityId(), articleId, articleInfo.getTitle(), advParam.getLineId(),
							advParam.getAccountId(), advParam.getNw(), advParam.getIp(), advParam.getDeviceType(),
							advParam.getStatsAct(), advParam.getLat(), advParam.getLng(),
							request.getParameter("linkRefer"));

			// 该文章加入黑名单中
			HashSet<String> curUserBlacklist = new HashSet<>();
			curUserBlacklist.add(articleId);

			ListIdsCache blockIds = new ListIdsCache(); // 之前不予展示的id
			String blockStr = (String) CacheUtil.getNew(AdvCache.getUserBlockContentIds(advParam.getUdid()));
			if (blockStr != null) {
				blockIds = JSON.parseObject(blockStr, ListIdsCache.class);
			}
			curUserBlacklist.addAll(blockIds.getIdList());
			blockIds.setIdList(new ArrayList<String>(curUserBlacklist));

			logger.info("点击的文章加入黑名单：blockIds={}", JSONObject.toJSON(blockIds));

			// 缓存用户看过的 文章黑名单 ids
			QueueObject objIds = new QueueObject();
			objIds.setKey(AdvCache.getUserBlockContentIds(advParam.getUdid())); // BLOCK#
			objIds.setTime(Constants.ONE_DAY_TIME);
			objIds.setArticleIds(blockIds);
			objIds.setQueueType(QueueCacheType.DISPLAY_IDS);
			Queue.set(objIds);

			// 跳转网页
			RequestDispatcher rd = request.getRequestDispatcher("article.jsp");
			rd.forward(request, response);

		} catch (Exception e) {
			e.printStackTrace();
			RequestDispatcher rd = request.getRequestDispatcher("404.jsp");
			rd.forward(request, response);
		}
	}

	/*
	 * 更新收藏频道
	 */
	@ResponseBody
	@RequestMapping(value = "adv!updateArticleChannels.action", produces = "text/plain;charset=UTF-8")
	public String updateArticleChannels(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws Exception {
		logger.info("update articleChannels ***********************************");
		String favArticleIds = request.getParameter("favArticleIds");
		AdvParam advParam = getActionParam(request);

		try {
			if (flowService.updateArticleChannels(favArticleIds, advParam)) {
				return serviceManager.getClienSucMap(new JSONObject(), Constants.STATUS_REQUEST_SUCCESS);
			} else {
				logger.error("udid={}, update articleChannels exception", advParam.getUdid());
				return serviceManager.getClienSucMap(new JSONObject(), Constants.STATUS_INTERNAL_ERROR);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			logger.error("udid={}, update articleChannels exception", advParam.getUdid());
		}
		return serviceManager.getClienSucMap(new JSONObject(), Constants.STATUS_INTERNAL_ERROR);
	}
	
	
	/*
	 * tab弹窗活动
	 */
	@ResponseBody
	@RequestMapping(value = "adv!getTabActivities.action", produces = "text/plain;charset=UTF-8")
	public String getTabActivities(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		AdvParam param = getActionParam(request);
		int entryId = getInt(request, "entryId");	//接口定义 0 Tab3, 1 Tab4。 与数据库定义小3（数据库需要默认值0作为不选择任何tab）
		try {
			TabEntity tabAdEntity = flowService.getTabActivities(param, entryId);
			if (tabAdEntity != null) {
				JSONObject j = new JSONObject();
				j.put("activity", tabAdEntity);
				return serviceManager.getClienSucMap(j, Constants.STATUS_REQUEST_SUCCESS);
			} else {
				return serviceManager.getClienSucMap(new JSONObject(), Constants.STATUS_REQUEST_SUCCESS);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return serviceManager.getClienSucMap(new JSONObject(), Constants.STATUS_INTERNAL_ERROR);
		}
		// return

	}
	
	
	
	/*
	 * 清除tab弹窗记录，供测试使用
	 * @String udid
	 * @int id
	 */
	@ResponseBody
	@RequestMapping(value = "adv!clearTabActivitiesRecord.action", produces = "text/plain;charset=UTF-8")
	public String clearTabActivitiesRecord(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		AdvParam param = getActionParam(request);
		String key = AdvCache.getTabActivitesKey(param.getUdid(), getInt(request, "id"));
		CacheUtil.deleteNew(key);
		logger.info("清理tab弹窗记录，udid={}, id={}, key={}", param.getUdid(), getInt(request, "id"), key);
		return serviceManager.getClienSucMap(new JSONObject(), Constants.STATUS_REQUEST_SUCCESS);
	}
}
