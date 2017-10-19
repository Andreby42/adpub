/*
 * Copyright 1999-2020 chelaile.com All right reserved. This software is the
 * confidential and proprietary information of chelaile.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with chelaile.com.
 */
package com.bus.chelaile.model;



/**
 * 类LineDetailAdMode.java的实现描述：
 *  线路详情页广告各个位置图片是否显示的模式。
 * @author liujh 2016年4月29日 上午9:42:14
 */
public enum LineDetailAdMode {
    TOP_RIGHT(1), // 右上角
    BIG_CAR(2), // 大车图
    SMALL_CAR(4), // 小车图
    LOWER_RIGHT(8), // 右下角
    BACKGROUND(16), // 背景色
    COLOR_BAR(32); // 彩条


    private int mask;

    private LineDetailAdMode(int mask){
        this.mask = mask;
    }

	public int getMask() {
		return mask;
	}

	public void setMask(int mask) {
		this.mask = mask;
	}
    
    
    
}
