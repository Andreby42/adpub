package com.bus.chelaile.alimama.response;


import java.util.List;

/**
 * Created by Administrator on 2016/10/8.
 */
public class AdContent {
    Integer aid;
    AdSet set;
    Integer tid;
    List<Creative> creative;

    public void print() {
        System.out.println("|----AID: " + aid);
        set.print();
        System.out.println("|----TID: " + tid);

        for (int i = 0; i < creative.size(); i++) {
            System.out.println(String.format("|----creative %d:", i));
            creative.get(i).print();
        }
    }

	public Integer getAid() {
		return aid;
	}

	public void setAid(Integer aid) {
		this.aid = aid;
	}

	public AdSet getSet() {
		return set;
	}

	public void setSet(AdSet set) {
		this.set = set;
	}

	public Integer getTid() {
		return tid;
	}

	public void setTid(Integer tid) {
		this.tid = tid;
	}

	public List<Creative> getCreative() {
		return creative;
	}

	public void setCreative(List<Creative> creative) {
		this.creative = creative;
	}




}