package com.bus.chelaile.model;


public enum ProductType {
	MANUAL("1", 1),  //手工
    GUANGDIANTONG("2", 2),  //广点通
    INMOBI("3", 3),    //inmobi
    ANWO("4", 4),		//安沃
    BAIDULIANMENG("5", 5),   //百度联盟
    AILIMAMA("6", 6),			// 阿里妈妈
    TOUTIAO("7", 7), // 今日头条
    YUEMENG("8", 8), // 阅盟
    ECOOOK("9", 9), // Ecoook
    KEDAXUNFEI("10", 10); //科大讯飞
    
    
    
    private String type;
    private String val;
    private int provider_id;
    
    private ProductType(String type, int provider_id) {
        this.type = type;
        this.val = type;
        this.provider_id = provider_id;
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

    public int getProvider_id() {
        return provider_id;
    }

    public void setProvider_id(int provider_id) {
        this.provider_id = provider_id;
    }
    
}
