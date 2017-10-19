package com.bus.chelaile.flow.model;

import java.util.ArrayList;
import java.util.List;

public class ListIdsCache {
	private long time;
	private ArrayList<String> idList = new ArrayList<String>();
	
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public ArrayList<String> getIdList() {
		return idList;
	}
	public void setIdList(ArrayList<String> idList) {
		this.idList = idList;
	}
	
	
	public void addIds(List<String> ids) {
		if(ids == null || ids.size() == 0) {
			return;
		}
		this.idList.addAll(ids);
	}
	
	public void clearIds() {
		if(this.idList == null) {
			return;
		}
		
		this.idList.clear();
	}
	
	@Override
	public String toString() {
		return "ListIdsCache [time=" + time + ", idList=" + idList + "]";
	}
}
