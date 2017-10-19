package com.bus.chelaile.alimama.response;


/**
 * Created by Administrator on 2016/10/8.
 */
public class Effect {
    Integer duration;
    Integer play_type;

    public void print() {
        System.out.println("|----|----|----Duration: " + duration);
        System.out.println("|----|----|----Play Type: " + play_type);
    }

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public Integer getPlay_type() {
		return play_type;
	}

	public void setPlay_type(Integer play_type) {
		this.play_type = play_type;
	}
    
}
