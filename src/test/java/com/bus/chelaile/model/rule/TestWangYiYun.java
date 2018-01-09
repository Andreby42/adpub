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
		advParam.setUdid("fb6d0547-b3ba-435b-ba29-001a1bbe261b");
		List<FlowContent> list =wangyiYun.getInfoByApi(advParam, 0l, null, 0, false);
		System.out.println(list.size());
		for(FlowContent flowContent: list) {
			System.out.println(flowContent.getUrl());
			System.out.println(flowContent.getTime());
		}
	}
}
