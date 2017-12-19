package com.bus.chelaile.flow;

import java.util.*;

import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.flow.model.FlowChannel;
import com.bus.chelaile.flow.model.FlowContent;

public interface InterfaceFlowHelp {
	/**
	 * 从第三方接口获得资讯
	 * @param <FlowNewContent>
	 */
	List<FlowContent> getInfoByApi(AdvParam advParam, long ftime, String recoid, int channelId, boolean isShowAd) throws Exception;
	
	/*
	 * 解析从数据库读取的渠道信息
	 */
	List<String> parseChannel(FlowChannel ucChannel);
	
	/*
	 * 处理参数，调用第三方接口
	 */
	List<FlowContent> parseResponse(AdvParam advParam, long ftime, String recoid, String token, String channelId, boolean isShowAd);

}
