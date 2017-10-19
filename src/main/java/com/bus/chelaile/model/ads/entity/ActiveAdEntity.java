package com.bus.chelaile.model.ads.entity;

import com.bus.chelaile.model.ShowType;

public class ActiveAdEntity extends BaseAdEntity {

	private String combpic; // 组合图片的链接。
	private String audioArrivedFile; //音频文件，到站提醒的
	private String audioArrivingFile; //即将到站的提醒音频
	private String arrivingText;
	private String arrivedText;
	private String aboardText;

	public ActiveAdEntity(ShowType showType) {
		super(showType.getValue());
		this.combpic = EMPTY_STR;
	}

	@Override
	protected ShowType gainShowTypeEnum() {
		if (showType == ShowType.RIDE_DETAIL.getValue()) {
			return ShowType.RIDE_DETAIL;
		} else if (showType == ShowType.CHAT_DETAIL.getValue()) {
			return ShowType.CHAT_DETAIL;
		} else if(showType == ShowType.RIDE_AUDIO.getValue()) {
			return ShowType.RIDE_AUDIO;
		}
		return ShowType.ACTIVE_DETAIL;
	}

	public String getCombpic() {
		return combpic;
	}

	public void setCombpic(String combpic) {
		this.combpic = combpic;
	}

	public String getAudioArrivingFile() {
		return audioArrivingFile;
	}

	public void setAudioArrivingFile(String audioArrivingFile) {
		this.audioArrivingFile = audioArrivingFile;
	}

	public String getAudioArrivedFile() {
		return audioArrivedFile;
	}

	public void setAudioArrivedFile(String audioArrivedFile) {
		this.audioArrivedFile = audioArrivedFile;
	}

	public String getArrivingText() {
		return arrivingText;
	}

	public void setArrivingText(String arrivingText) {
		this.arrivingText = arrivingText;
	}

	public String getArrivedText() {
		return arrivedText;
	}

	public void setArrivedText(String arrivedText) {
		this.arrivedText = arrivedText;
	}

	public String getAboardText() {
		return aboardText;
	}

	public void setAboardText(String aboardText) {
		this.aboardText = aboardText;
	}

}
