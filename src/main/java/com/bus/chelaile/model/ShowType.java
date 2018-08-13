package com.bus.chelaile.model;



public enum ShowType {
    DOUBLE_COLUMN("00"),  //双栏，站级别广告
    SINGLE_COLUMN("01"),  //单栏，线级别广告
    FULL_SCREEN("02"),    //全屏广告,浮层
    PUSH_NOTICE("03"),
    OPEN_SCREEN("04"),    //开屏广告
    LINE_DETAIL("05"),    //线路详情页广告
    LINE_DETAIL_PIC("06"),	//线路详情页的图片
    ACTIVE_DETAIL("07"),    //活动页广告，对应投放系统：乘车报告banner
    RIDE_DETAIL("08"),   //乘车页广告，对应投放系统：乘车页面引流入口
    CHAT_DETAIL("09"),		//聊天室广告
    FLOW_ADV("10"),			//10 留给详情页信息流广告
    FULL_SCREEN_RIDE("11"),    //乘车页的全屏广告,新增的一种浮层
    RIDE_AUDIO("12"),		//下车提醒的音频广告
    FULL_SCREEN_MOBIKE("13"),		//共享单车页的全屏广告，新增的一种浮层
    ROUTE_PLAN_ADV("14"),		//线路规划页广告
    STATION_ADV("15"),		// 站点广告
    FEED_ADV("16"),		// feed流广告
    LINEDETAIL_REFRESH_ADV("17"),   //详情页刷新位广告
    H5_LINEBANNER_ADV("18"), // h5 详情页banner广告
    LINEDETAIL_REFRESH_OPEN_ADV("19"), // 详情页下拉刷新位，全屏广告
    WECHATAPP_BANNER_ADV("20"), // 小程序 banner位广告
    WECHAT_FULL_ADV("21"),  // 小程序浮层广告
    LINE_FEED_ADV("22"),    // 详情页底部广告
    LINE_RIGHT_ADV("23"),   // 详情页右上角广告位
	TRANSFER_ADV("24"),		//路线换乘
	CAR_ALL_LINE_ADV("25"),	//同站线路
	ALL_CAR_ADV("26"),		//更多车辆
	GUIDE_ADV("27"),         //导流位广告
	INTERSHOME_ADV("28"),     //首页页插屏
	INTERSTRANSIT_ADV("29"),     //路线页插屏
	INTERSENERGY_ADV("30"),     //福利社插屏
	INTERSMINE_ADV("31");     //我的页插屏
    
    private String type;
    private int val;
    
    private ShowType(String type) {
        this.type = type;
        this.val = Integer.parseInt(type);
    }
    
    public String getType(){
    	return type;
    }
    
    public int getValue() {
        return val;
    }
    
    public static ShowType from(String type) {
    	
    	
        if (type == null) {
            return null;
        }
        for (ShowType sType : values()) {
            if (sType.type.equals(type)) {
                return sType;
            }
        }
        return null;
    }
}
