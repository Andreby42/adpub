package com.bus.chelaile.mvc;

import javax.servlet.http.HttpServletRequest;




import org.apache.commons.lang3.StringUtils;

import com.bus.chelaile.push.pushModel.SinglePushParam;

public class AbstractController {

	public AdvParam getActionParam(HttpServletRequest request) {
		AdvParam param = new AdvParam();
		param.setUserId(request.getParameter("userId"));
		param.setAccountId(request.getParameter("accountId"));
		param.setS(request.getParameter("s"));
		param.setSv(request.getParameter("sv"));
		param.setUdid(request.getParameter("udid"));
		param.setV(request.getParameter("v"));
		param.setVc(getInt(request, "vc"));
		param.setNw(request.getParameter("nw"));
		param.setFirst_src(request.getParameter("first_src"));
		param.setLng(getDouble(request, "lng"));
		param.setLat(getDouble(request, "lat"));
		param.setDeviceType(request.getParameter("deviceType"));
		param.setIdfa(request.getParameter("idfa"));
		param.setImei(request.getParameter("imei"));
		param.setUa(request.getParameter("userAgent"));
		param.setO1(request.getParameter("o1"));

		param.setLineId(request.getParameter("lineId"));
		param.setLineNo(request.getParameter("lineNo"));
		param.setLineName(request.getParameter("lineName"));

		param.setScreenHeight(getInt(request, "screenHeight"));
		param.setCityId(request.getParameter("cityId"));
		param.setH5Src(request.getParameter("h5_src"));
		param.setH5User(request.getParameter("h5_user"));
		if(StringUtils.isNoneBlank(param.getH5User()) && ! param.getH5User().equals("null")) {
			param.setUdid(param.getH5User());
		}
		param.setShareId(request.getParameter("shareId"));
		
		param.setStatsAct(request.getParameter("stats_act"));

		if (request.getParameter("ip") != null && !request.getParameter("ip").equals("")) {
			param.setIp(request.getParameter("ip"));
		} else {
			param.setIp(request.getParameter("remote_addr"));
		}

		return param;
	}

	public SinglePushParam getPushParam(HttpServletRequest request) {
		SinglePushParam singlePushParam = new SinglePushParam();
		
		String body = request.getParameter("body");
		String link = request.getParameter("link");
		String pushKey = request.getParameter("pushKey");
		String pushType = request.getParameter("pushType");
		String title = request.getParameter("title");
		
		singlePushParam.setBody(body == null ? "" : body);
		singlePushParam.setLink(link == null ? "" : link);
		singlePushParam.setPushKey(pushKey == null ? "" : pushKey);
		singlePushParam.setPushType(pushType);
		singlePushParam.setTitle(title == null ? "" : title);
		
		return singlePushParam;
	}

	protected static int getInt(HttpServletRequest request, String paramName) {
		String value = request.getParameter(paramName);
		if (value == null || value.length() == 0) {
			return -1;
		} else {
			try {
				return Integer.parseInt(value);
			} catch (Exception e) {
				return -1;
			}
		}
	}
	
	protected static long getLong(HttpServletRequest request, String paramName) {
		String value = request.getParameter(paramName);
		if (value == null || value.length() == 0) {
			return 0L;
		} else {
			try {
				return Long.parseLong(value);
			} catch (Exception e) {
				return 0L;
			}
		}
	}
	
	protected static Double getDouble(HttpServletRequest request, String paramName) {
		String value = request.getParameter(paramName);
		if (value == null || value.length() == 0) {
			return -1.0;
		} else {
			try {
				return Double.parseDouble(value);
			} catch (Exception e) {
				return -1.0;
			}
		}
	}
	
	/*
	 * 返回布尔类型的参数值
	 */
	protected static boolean getBoolean(HttpServletRequest request, String paramName) {
		String value = request.getParameter(paramName);
		if(StringUtils.isBlank(paramName)) {
			return false;
		} else {
			try{
				return Boolean.parseBoolean(value);
			} catch(Exception e) {
				return false;
			}
		}
	}
}
