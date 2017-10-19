package com.bus.chelaile.mvc;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.bus.chelaile.common.Constants;
import com.bus.chelaile.push.AdsPushService;
import com.bus.chelaile.push.NoticePushService;
import com.bus.chelaile.push.SinglePushService;
import com.bus.chelaile.push.pushModel.SinglePushParam;
import com.bus.chelaile.service.ServiceManager;

/**
 * push 相关接口
 * 
 * @author zzz
 * 
 */

@Controller
@RequestMapping("")
public class BusPushAction extends AbstractController {

	private static final Logger logger = LoggerFactory.getLogger(BusPushAction.class);

	@Autowired
	private AdsPushService adsPushService;
	@Autowired
	private NoticePushService noticePushService;
	@Autowired
	private SinglePushService singlePushService;

	@Autowired
	private ServiceManager serviceManager;

	/*
	 * 广告推送
	 */
	@ResponseBody
	@RequestMapping(value = "notice!newNotice.action", produces = "Content-Type=text/plain;charset=UTF-8")
	public String newNotice(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws Exception {
		long st = System.currentTimeMillis();
		int id = Integer.valueOf(getInt(request, "advId"));
		String ruleId = request.getParameter("ruleId");
		String ruleIds = request.getParameter("ruleIds");
		String type = request.getParameter("type");	//type==inside的时候，只给内部用户推送。 否则，正常推送
		boolean isPushAndroidAll = getBoolean(request, "isPushAndroidAll"); //是否推送给android全量用户。ture，是；false，不是
		
		// 推送
		if (adsPushService.pushAds(id, ruleId, ruleIds, type, isPushAndroidAll)) {
			logger.info("push success: adv={}, ruleId={} costs {} ms", id, ruleId, (System.currentTimeMillis() - st));
			return serviceManager.getClienSucMap(new JSONObject(), Constants.STATUS_REQUEST_SUCCESS);
		} else {
			logger.info("push failed: adv={}, ruleId={} costs {} ms", id, ruleId, (System.currentTimeMillis() - st));
			return serviceManager.getClientErrMap("", Constants.STATUS_INTERNAL_ERROR);
		}

	}

	/*
	 * 话题推送
	 */
	@ResponseBody
	@RequestMapping(value = "notice!pushFeedInfo.action", produces = "Content-Type=text/plain;charset=UTF-8")
	public String pushFeedInfo(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws Exception {

		String udid = request.getParameter("udid");
		String pushType = request.getParameter("pushType");
		String badge = request.getParameter("badge");

		try {
			if (StringUtils.isNotBlank(udid) && StringUtils.isNotBlank(pushType)) {

				SinglePushParam singlePushParam = getPushParam(request);
				// 推送
				if (noticePushService.noticePushFeedInfo(singlePushParam, udid, badge)) {
					return serviceManager.getClienSucMap(new JSONObject(), Constants.STATUS_REQUEST_SUCCESS);
				} else {
					return serviceManager.getClientErrMap("", "01");
				}

			} else {
				return serviceManager.getClientErrMap("参数异常", Constants.STATUS_PARAM_ERROR);
			}
		} catch (Throwable e) {
			logger.error("目标异常： " + e.getMessage(), e);
			logger.error("noticePush exception:{}", e);
			return serviceManager.getClientErrMap(e.getMessage(), "-1");
		}
	}

	/*
	 * 对单个用户的消息推送
	 */
	@ResponseBody
	@RequestMapping(value = "notice!noticePush.action", produces = "Content-Type=text/plain;charset=UTF-8")
	public String noticePush(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws Exception {

		String udid = request.getParameter("udid");
		String pushType = request.getParameter("pushType");

		try {
			if (StringUtils.isNotBlank(udid) && udid.length() < 64 && StringUtils.isNotBlank(pushType)) {
				SinglePushParam singlePushParam = getPushParam(request);
				if (singlePushService.noticePushBySingleUdid(singlePushParam, udid)) {
					return serviceManager.getClienSucMap(new JSONObject(), Constants.STATUS_REQUEST_SUCCESS);
				} else {
					return serviceManager.getClientErrMap("", "01");
				}
			} else {
				return serviceManager.getClientErrMap("参数异常", Constants.STATUS_PARAM_ERROR);
			}
		} catch (Throwable e) {
			logger.error("目标异常： " + e.getMessage(), e);
			logger.error("noticePush exception:{}", e);
			return serviceManager.getClientErrMap(e.getMessage(), "-1");

		}
	}

	
	
	@RequestMapping(value = "forwardShortUrl.action")
	public String forwardShortUrl(HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {
		
		String shortUrl = request.getParameter("shortUrl");
		
		String url = "http://m.chelaile.net.cn";
		
		
		
		if (StringUtils.isBlank(shortUrl)) {
			
			logger.error("shortUrl is null");
			return "redirect:"+url;
		}
		try {
			url = adsPushService.getUrlFromShortUrl(shortUrl.replaceAll("/",
					""));
			if (url != null) {
				StringBuffer buffer = new StringBuffer();
				Enumeration<?> enu=request.getParameterNames();  
				while(enu.hasMoreElements()){  
				String paraName=(String)enu.nextElement();
				buffer.append(paraName);
				buffer.append("=");
				buffer.append(request.getParameter(paraName));
				buffer.append("&");
				//System.out.println(paraName+": "+request.getParameter(paraName));  
				}
				String str = buffer.toString();
				url += "&"+str.substring(0,str.length()-1);
				logger.info("goFromShortUrl shortUrl:{}, realUrl:{}", shortUrl,
						url);
			} else {
				url = "http://m.chelaile.net.cn";
				logger.info("goFromShortUrl shortUrl:{} is null", shortUrl);
			}
		} catch (Exception e) {
			logger.info("goFromShortUrl shortUrl:{}, is error", shortUrl);
			//realUrl = "http://m.chelaile.net.cn";
			logger.error(e.getMessage(), e);
		}

		return "redirect:"+url;
	}
}
