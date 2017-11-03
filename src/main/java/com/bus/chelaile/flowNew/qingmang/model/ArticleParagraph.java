package com.bus.chelaile.flowNew.qingmang.model;

public class ArticleParagraph {

	private String id;
	private int type;
	private ArticleText text;
	private ArticleImage image;
	
	@Override
	public String toString() {
		return "ArticleParagraph [id=" + id + ", type=" + type + ", text=" + text + ", image=" + image + "]";
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public ArticleText getText() {
		return text;
	}
	public void setText(ArticleText text) {
		this.text = text;
	}
	public ArticleImage getImage() {
		return image;
	}
	public void setImage(ArticleImage image) {
		this.image = image;
	}
	
	
	
}
