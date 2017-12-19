package com.bus.chelaile.model;



public enum Platform {
    IOS("ios", "IOS"),
    ANDROID("android", "android"),
    H5("h5", "h5"),
    YM("androidyoumeng", "androidyoumeng"),
    GT("androidgetui", "androidgetui"),
    JG("androidjpush", "androidjpush");


    private String value;

    private String display;
    
    private Platform(String v, String display) {
        value = v;
        this.display = display;
    }
    
    public boolean isH5(String platform) {
        return H5.value.equalsIgnoreCase(platform);
    }
    
    public boolean isIOS(String platform) {
        return IOS.value.equalsIgnoreCase(platform);
    }
    
    public boolean isAndriod(String platform) {
        return ANDROID.value.equalsIgnoreCase(platform);
    }
    
    /**
     * make sure the input plat is in lower case.
     * @param plat plat should be in lower case.
     * @return
     */
    public static Platform from(String plat) {
        if (plat == null) {
            return ANDROID;
        }
        if (ANDROID.value.equalsIgnoreCase(plat)) {
            return ANDROID;
        }
        if (IOS.value.equalsIgnoreCase(plat)) {
            return IOS;
        }
        if (H5.value.equalsIgnoreCase(plat)) {
            return H5;
        }
        
        return null;
    }

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}
    
    
}
