package com.bus.chelaile.flow.model;

import java.util.ArrayList;

import com.bus.chelaile.util.DateUtil;

public class StreamForm {

	private String day;
	private int number;		//点击文章数目
	private ArrayList<StreamFormData> dataList = new ArrayList<StreamFormData>();  //点击文章详情
//	private int subType;
	
	public StreamForm() {
		super();
		this.day = DateUtil.getTodayStr("yyy-MM-dd");
	}
	

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}


	public ArrayList<StreamFormData> getDataList() {
		return dataList;
	}


	public void setDataList(ArrayList<StreamFormData> dataList) {
		this.dataList = dataList;
	}
	
	public void addData(StreamFormData data) {
		this.dataList.add(data);
	}
	
	public void clearData() {
		this.dataList.clear();
	}

}
