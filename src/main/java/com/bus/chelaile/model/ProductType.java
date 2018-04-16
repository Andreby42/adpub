package com.bus.chelaile.model;


public enum ProductType {
	MANUAL("1"),  //手工
    GUANGDIANTONG("2"),  //广点通
    INMOBI("3"),    //inmobi
    ANWO("4"),		//安沃
    BAIDULIANMENG("5"),   //百度联盟
    AILIMAMA("6"),			// 阿里妈妈
    TOUTIAO("7"), // 今日头条
    YUEMENG("8"), // 阅盟
    ECOOOK("9"), // Ecoook
    KEDAXUNFEI("10"); //科大讯飞
    
    
    
    private String type;
    private String val;
    
    private ProductType(String type) {
        this.type = type;
        this.val = type;
    }
    
    public String getValue() {
        return val;
    }
    
    public static ProductType from(String type) {
        if (type == null) {
            return null;
        }
        for (ProductType sType : values()) {
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

	public String getVal() {
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}
    
}
