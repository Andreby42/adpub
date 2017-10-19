package com.bus.chelaile.model;



/**
 * 用户类型
 * @author liujh
 *
 */
public enum UserType {
    ALL(0),       // 所有用户
    NEW(1),       // 新用户
    ANONYMOUS(2), // 匿名用户
    NOT_NEW(3),    // 非新用户
    TODAY_NEW(4);  // 当日新增用户

    
    private int type;
    private UserType(int t) {
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
    
    
    
}
