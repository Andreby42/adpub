package com.bus.chelaile.alimama.response;




/**
 * Created by Administrator on 2016/10/8.
 */
public class AdSet {
    Integer atype;
    AdSetting setting;

    public void print() {
        System.out.println("|----Ad set: ");
        System.out.println("|----|----AType: " + atype);
        System.out.print("|----|----Setting: ");
        if (setting == null) {
            System.out.println("null");
        } else {
            System.out.println();
            setting.print();
        }
    }

	public Integer getAtype() {
		return atype;
	}

	public void setAtype(Integer atype) {
		this.atype = atype;
	}

	public AdSetting getSetting() {
		return setting;
	}

	public void setSetting(AdSetting setting) {
		this.setting = setting;
	}


}
