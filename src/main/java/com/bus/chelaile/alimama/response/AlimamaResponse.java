package com.bus.chelaile.alimama.response;


import java.util.List;

/**
 * Created by Administrator on 2016/9/27.
 */
public class AlimamaResponse {
    private List<AdContent> ad;
    private String extdata;
    private String status;
    private String errcode;

    public boolean getAdSuccessfully() {
        return status.equalsIgnoreCase("ok");
    }

    public void print() {
        System.out.println("Ext data: " + extdata);
        System.out.println("Status: " + status);
        System.out.println("Error code: " + errcode);
        System.out.println("Ads: ");
        for (int i = 0; i < ad.size(); i++) {
            System.out.println(String.format("===========Ad %d=============", i));
            ad.get(i).print();
        }
    }

	public List<AdContent> getAd() {
		return ad;
	}

	public void setAd(List<AdContent> ad) {
		this.ad = ad;
	}

	public String getExtdata() {
		return extdata;
	}

	public void setExtdata(String extdata) {
		this.extdata = extdata;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getErrcode() {
		return errcode;
	}

	public void setErrcode(String errcode) {
		this.errcode = errcode;
	}


}
