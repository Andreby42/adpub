package com.bus.chelaile.model.record;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.bus.chelaile.model.DisplayAdvInfo;
import com.bus.chelaile.util.New;



public class DisplayUserCache {
	// 可以显示的广告集合
	private List<DisplayAdvInfo> advList = New.arrayList();
	// 不敢兴趣集合
	private Map<String, String> uninterestedMap = New.hashMap();

	public void createAdvInfo(String title, String showType, int advId) {
		DisplayAdvInfo info = new DisplayAdvInfo();
		info.setTitle(title);
		info.setShowType(showType);
		info.setAdvId(advId);
		advList.add(info);
	}

	public void createUninterested(Map<String, uninterested> map) {
		if( map == null ){
			return;
		}
		for (Map.Entry<String, uninterested> entry : map.entrySet()) {
			long time = entry.getValue().getTime();
			Date date = new Date(time);
			SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String uninterestedTime = dFormat.format(date);
			uninterestedMap.put(entry.getKey(), uninterestedTime);
		}
	}

	public List<DisplayAdvInfo> getAdvList() {
		return advList;
	}

	public void setAdvList(List<DisplayAdvInfo> advList) {
		this.advList = advList;
	}

	public Map<String, String> getUninterestedMap() {
		return uninterestedMap;
	}

	public void setUninterestedMap(Map<String, String> uninterestedMap) {
		this.uninterestedMap = uninterestedMap;
	}

}


