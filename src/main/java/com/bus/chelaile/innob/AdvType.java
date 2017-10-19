package com.bus.chelaile.innob;


public enum AdvType {
	MANUAL("1"),  //	我们自己广告
    GUANGDIANTONG("2"),  //	调用广点通广告
    API("3"),    //	调用第三方广告
    BAIDU("4");		// 百度sdk
    
    
    private String type;
    private int val;
    
    private AdvType(String type) {
        this.setType(type);
        this.setVal(Integer.parseInt(type));
    }
    
    public int getValue() {
        return getVal();
    }
    
    public static AdvType from(String type) {
        if (type == null) {
            return null;
        }
        for (AdvType sType : values()) {
            if (sType.getType().equals(type)) {
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
