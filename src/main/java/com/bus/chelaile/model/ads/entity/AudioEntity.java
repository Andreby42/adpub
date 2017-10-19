package com.bus.chelaile.model.ads.entity;

import com.bus.chelaile.model.ShowType;

public class AudioEntity extends BaseAdEntity {

	private String audioFile; // 音频文件

	public AudioEntity(ShowType showType) {
		super(showType.getValue());
		this.setAudioFile(EMPTY_STR);
	}

	@Override
	protected ShowType gainShowTypeEnum() {
		return ShowType.RIDE_AUDIO;
	}

	public String getAudioFile() {
		return audioFile;
	}

	public void setAudioFile(String audioFile) {
		this.audioFile = audioFile;
	}
}
