package com.bus.chelaile.model.ads;

public class AdTagInfo {

	private String tagText;
	private String tagBG;
	private String tagPic;
	public String getTagText() {
		return tagText;
	}
	public void setTagText(String tagText) {
		this.tagText = tagText;
	}
	public String getTagBG() {
		return tagBG;
	}
	public void setTagBG(String tagBG) {
		this.tagBG = tagBG;
	}
	public String getTagPic() {
		return tagPic;
	}
	public void setTagPic(String tagPic) {
		this.tagPic = tagPic;
	}
	public AdTagInfo() {
		super();
	}
	public AdTagInfo(String tagText, String tagBG, String tagPic) {
		super();
		this.tagText = tagText;
		this.tagBG = tagBG;
		this.tagPic = tagPic;
	}
}
