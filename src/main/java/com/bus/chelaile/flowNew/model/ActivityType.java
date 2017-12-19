package com.bus.chelaile.flowNew.model;



/**
 * 活动类型
 * @author liujh
 *
 */

public enum ActivityType {
	    H5(1),       		// H5
	    TAG_HOME(2),       	// 话题首页
	    TAG(3), 			// 话题标签页
	    DUIBA_HOME(5),   	// 积分商城首页
	    DUIBA_DETAIL(6),	// 积分商城详情页
	    TAG_DETAIL(7);		// 话题详情页
	    
	    private int type;
	    private ActivityType(int t) {
	        type = t;
	    }
	    
	    public static boolean isAnonymous(int type) {
	        return false;
	    }
	    
	    public static boolean isRegistered(int type) {
	        return false;
	    }
	    
	    public static boolean isNew(int type) {
	        return false;
	    }

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}
	    
	    
	    public static void main(String[] args) {
	    	System.out.println(ActivityType.H5.getType());
	    }


}
