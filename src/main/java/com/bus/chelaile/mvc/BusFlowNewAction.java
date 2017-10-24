package com.bus.chelaile.mvc;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bus.chelaile.flowNew.FlowServiceManager;

/*
 * 新版信息流相关接口
 */
@Controller
@RequestMapping("")
public class BusFlowNewAction extends AbstractController{

	
	@Resource
	private FlowServiceManager flowServiceManager;
	
	@ResponseBody
	@RequestMapping(value = "flow!getLineDetailFlows.action", produces = "Content-Type=text/plain;charset=UTF-8")
	public String getLineDetailFlows(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		
		AdvParam param = getActionParam(request);
		
		return flowServiceManager.getResponseLineDetailFlows(param);
	}
	
}
