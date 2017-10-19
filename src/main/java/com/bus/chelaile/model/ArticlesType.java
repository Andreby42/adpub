package com.bus.chelaile.model;

public enum ArticlesType {
	ARTICLES("articles"),
	SPECIALS("specials");
	
	ArticlesType(String type) {
		this.setType(type);
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	private String type;


}
