package com.bus.chelaile.model.rule;

import java.util.List;

import com.bus.chelaile.flow.WangYiYunHelp;
import com.bus.chelaile.flow.model.FlowContent;
import com.bus.chelaile.mvc.AdvParam;

public class TestWangYiYun {
	public static void main(String[] args) throws Exception {
		WangYiYunHelp wangyiYun = new WangYiYunHelp();
		//575042c1eac22112dcab5620179035829b878a91
		AdvParam advParam = new AdvParam();
		advParam.setUdid("575042c1eac22112dcab5620179035829b878a91");
		List<FlowContent> list =wangyiYun.getInfoByApi(advParam, 0l, null, 0, false);
		System.out.println(list.size());
		for(FlowContent flowContent: list) {
			System.out.println(flowContent.getUrl());
		}
	}
}
