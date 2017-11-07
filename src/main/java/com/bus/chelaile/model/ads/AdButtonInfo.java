package com.bus.chelaile.model.ads;

public class AdButtonInfo {
	
	private String buttonText;
	private String buttonColor;
	private String buttonBG;
	private String buttonRim;
	private String buttonPic;
	public String getButtonText() {
		return buttonText;
	}
	public AdButtonInfo(String buttonText, String buttonColor, String buttonBG, String buttonRim, String buttonPic) {
		super();
		this.buttonText = buttonText;
		this.buttonColor = buttonColor;
		this.buttonBG = buttonBG;
		this.buttonRim = buttonRim;
		this.buttonPic = buttonPic;
	}
	public void setButtonText(String buttonText) {
		this.buttonText = buttonText;
	}
	public String getButtonColor() {
		return buttonColor;
	}
	public void setButtonColor(String buttonColor) {
		this.buttonColor = buttonColor;
	}
	public String getButtonBG() {
		return buttonBG;
	}
	public void setButtonBG(String buttonBG) {
		this.buttonBG = buttonBG;
	}
	public String getButtonRim() {
		return buttonRim;
	}
	public void setButtonRim(String buttonRim) {
		this.buttonRim = buttonRim;
	}
	public String getButtonPic() {
		return buttonPic;
	}
	public void setButtonPic(String buttonPic) {
		this.buttonPic = buttonPic;
	}
	public AdButtonInfo() {
		super();
	}
}
