package com.bus.chelaile.mvc;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.common.AdvCache;
import com.bus.chelaile.common.CacheUtil;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.flowNew.FlowServiceManager;
import com.bus.chelaile.flowNew.FlowStaticContents;
import com.bus.chelaile.util.DateUtil;

/*
 * 新版信息流相关接口
 */
@Controller
@RequestMapping("")
public class BusFlowNewAction extends AbstractController {

	protected static final Logger logger = LoggerFactory.getLogger(BusFlowNewAction.class);
	@Resource
	private FlowServiceManager flowServiceManager;

	/**
	 * 获取详情页下方滚动单栏内容
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "flow!getLineDetailFlows.action", produces = "Content-Type=text/plain;charset=UTF-8")
	public String getLineDetailFlows(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		AdvParam param = getActionParam(request);

		return flowServiceManager.getResponseLineDetailFlows(param);
	}

	/**
	 * 获取文章列表
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "flow!getArticleList.action", produces = "Content-Type=text/plain;charset=UTF-8")
	public String getArticleList(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		AdvParam param = getActionParam(request);
		String channelId = request.getParameter("channelId");
		String articleId = request.getParameter("articleId");

		if (!flowServiceManager.paramCheck(param, channelId)) {
			logger.error("参数异常 ");
			return flowServiceManager.getClientErrMap("参数异常", Constants.STATUS_PARAM_ERROR);
		}

		logger.info("channelId={}, articleId={}", channelId, articleId);
		return flowServiceManager.getResponseArticleList(param, channelId, articleId);
	}

	/**
	 * 查询过杂志的真实‘阅读过’人数
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "flow!getChannelReadNum.action", produces = "Content-Type=text/plain;charset=UTF-8")
	public String getChannelReadNum(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		AdvParam param = getActionParam(request);
		String channelId = request.getParameter("channelId");

		logger.info("QM获取杂志阅读人数，channelId={}", channelId);
		return flowServiceManager.getChannelReadNumResponse(param, channelId);
	}

	/**
	 * 记录阅读过杂志的人数
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "flow!recordChannelClick.action", produces = "Content-Type=text/plain;charset=UTF-8")
	public String recordChannelClick(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		AdvParam param = getActionParam(request);
		String channelId = request.getParameter("channelId");

		logger.info("QM点击杂志， channelId={}, acId={}", channelId, param.getAccountId());
		return flowServiceManager.recordChannleClick(param, channelId);
	}

	/**
	 * 记录阅读文章
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "flow!recordArticleClick.action", produces = "Content-Type=text/plain;charset=UTF-8")
	public String recordArticleClick(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		AdvParam param = getActionParam(request);
		String channelId = request.getParameter("channelId");
		String articleId = request.getParameter("articleId");

		logger.info("QM点击文章，channelId={}, articleId={}", channelId, articleId);
		return flowServiceManager.recordArticleClick(param, channelId, articleId);
	}

	/**
	 * 分享杂志
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "flow!updateShareStatus.action", produces = "Content-Type=text/plain;charset=UTF-8")
	public String updateShareStatus(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		AdvParam param = getActionParam(request);
		String channelId = request.getParameter("channelId");

		logger.info("QM分享杂志， channelId={},acId={}", channelId, param.getAccountId());
		return flowServiceManager.recordPersonChannleShared(param, channelId);
	}

	// 仅 测试~
	/**
	 * 清除用户阅读文章记录的次数
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "flow!clearArticleClickRecord.action", produces = "Content-Type=text/plain;charset=UTF-8")
	public String clearArticleClickRecord(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		AdvParam param = getActionParam(request);
		String channelId = request.getParameter("channelId");

		String keyPerson = "QM_PERSON_ARTICLE_READ_" + param.getAccountId() + "_" + channelId;
		CacheUtil.deleteNew(keyPerson);
		return flowServiceManager.getClienSucMap(new JSONObject(), Constants.STATUS_REQUEST_SUCCESS);
	}

	@ResponseBody
	@RequestMapping(value = "flow!clearArticleSharedRecord.action", produces = "Content-Type=text/plain;charset=UTF-8")
	public String clearArticleSharedRecord(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		AdvParam param = getActionParam(request);
		String channelId = request.getParameter("channelId");

		String key = "QM_SHARED_" + param.getAccountId() + "_" + channelId;
		CacheUtil.deleteNew(key);
		return flowServiceManager.getClienSucMap(new JSONObject(), Constants.STATUS_REQUEST_SUCCESS);
	}

	/**
	 * 将文章记录的第一篇no重置为0，让缓存从第一篇开始下载
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 */

	@ResponseBody
	@RequestMapping(value = "flow!clearArticleNoRecord.action", produces = "Content-Type=text/plain;charset=UTF-8")
	public String clearArticleNoRecord(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		String date = DateUtil.getTodayStr("yyyy-MM-dd");
		for (String channelId : FlowStaticContents.CHANNELS) {
			CacheUtil.setNew(AdvCache.getQMArticleNo(date + "#" + channelId), Constants.ONE_DAY_TIME, "0");
		}

		return flowServiceManager.getClienSucMap(new JSONObject(), Constants.STATUS_REQUEST_SUCCESS);
	}

}
