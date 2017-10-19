package com.bus.chelaile.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bus.chelaile.flow.model.FlowContent;

public class FlowUtil {
	private static final Logger logger = LoggerFactory.getLogger(FlowUtil.class);
	/**
	 * 设置图片大小模式，避免样式单调
	 * @param contents
	 */
	public static void setImagsType(List<FlowContent> contents) {
		try {
			if (contents == null || contents.size() == 0) {
				return;
			}

			int canBeBig = 0;
			int mustBeBig = 4;
			Random random = new Random();
			for (FlowContent uc : contents) {
//				if(Constants.ISTEST && tag == 2) {		
//					uc.setImgsType(1);
//					canBeBig = 2;
//					mustBeBig = 4;
//				}
				
				int j = random.nextInt(3);		 // 产生0、1、2随机一个
				if(uc.getImgsType() == 1 || uc.getImgsType() == 3) {		 //大图保持不变(包括活动)
					canBeBig = 2;
					mustBeBig = 4;
					continue;
				}
				
				if (j == 0 && canBeBig <= 0 || mustBeBig <= 0 ) {
					if (uc.getImgs().size() >= 3) {
						uc.setImgsType(2);
						canBeBig = 2;
						mustBeBig = 4;
					} 
					// 测试用
					else {
						uc.setImgsType(1);
						canBeBig = 2;
						mustBeBig = 4;
					}
				} else {
					uc.setImgsType(0);
					mustBeBig--;
					canBeBig--;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("设置图片大小模式出错！ ");
		}
	}
	
	/*
	 * 把数组转换成类似1,2,3,4的字符串。用户保存至ocs中
	 */
	public static String changeArrayTOString(ArrayList<String> favChannelIds) {
		if(favChannelIds == null || favChannelIds.size() == 0) {
			return null;
		}
		
		StringBuilder str = new StringBuilder();
		for(int i = 0; i < favChannelIds.size(); i ++) {
			str.append(favChannelIds.get(i));
			if(i < favChannelIds.size() - 1) {
				str.append(",");
			}
		}
		return str.toString();
	}

	
	
	
	public static void main(String[] args) {
	}

}
