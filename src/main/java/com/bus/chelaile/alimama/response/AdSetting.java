package com.bus.chelaile.alimama.response;


/**
 * Created by Administrator on 2016/10/8.
 */
public class AdSetting {
    Integer frame_index;
    Integer display_time;
    Integer enable_click;
    Integer enable_skip;

    public void print() {
        System.out.println("|----|----|----Frame index: " + frame_index);
        System.out.println("|----|----|----Display time: " + display_time);
        System.out.println("|----|----|----Enable_click: " + enable_click);
        System.out.println("|----|----|----Enable_skip: " + enable_skip);
    }

	public Integer getFrame_index() {
		return frame_index;
	}

	public void setFrame_index(Integer frame_index) {
		this.frame_index = frame_index;
	}

	public Integer getDisplay_time() {
		return display_time;
	}

	public void setDisplay_time(Integer display_time) {
		this.display_time = display_time;
	}

	public Integer getEnable_click() {
		return enable_click;
	}

	public void setEnable_click(Integer enable_click) {
		this.enable_click = enable_click;
	}

	public Integer getEnable_skip() {
		return enable_skip;
	}

	public void setEnable_skip(Integer enable_skip) {
		this.enable_skip = enable_skip;
	}
    
}
