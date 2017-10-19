package com.bus.chelaile.innob;


public enum ApiType {
	NATIVE("1"),  //
    BANNER("2"),  //
    IMG("3"),    //
    OPEN("4"),		//开屏
    FLOAT("5");		//浮层
    
    
    private String type;
    private int val;
    
    private ApiType(String type) {
        this.type = type;
        this.val = Integer.parseInt(type);
    }
    
    public int getValue() {
        return val;
    }
    
    public static ApiType from(String type) {
        if (type == null) {
            return null;
        }
        for (ApiType sType : values()) {
            if (sType.type.equals(type)) {
                return sType;
            }
        }
        return null;
    }

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getVal() {
		return val;
	}

	public void setVal(int val) {
		this.val = val;
	}
    
}
