package com.bus.chelaile.model;



/**
 * 用户类型
 * @author liujh
 *
 */
public enum TypeNumber {
    ZERO(0),       	// 0
    ONE(1),       	// 1
    TOW(2), 		// 2
    THREE(3),   	 // 3
    FOUR(4); 	 	// 4

    
    private int type;
    private TypeNumber(int t) {
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
    	System.out.println(TypeNumber.ONE.getType());
    }
}
