package com.bus.chelaile.alimama.response;


import java.util.List;

/**
 * Created by Administrator on 2016/10/8.
 */
public class Creative {
    String cid;
    Effect effects;
    String mid;
    Media media;
    List<String> impression;
    List<String> click;
    List<String> download;
    List<String> playstart;
    String event;

    public void print() {
        System.out.println("|----|----CID: " + cid);
        System.out.print("|----|----Effect: ");
        if (effects == null) {
            System.out.println("null");
        } else {
            System.out.println();
            effects.print();
        }
        System.out.println("|----|----MID: " + mid);
        System.out.print("|----|----Media: ");
        if (media == null) {
            System.out.println("null");
        } else {
            System.out.println();
            media.print();
        }
        System.out.println("|----|----Impressions: ");
        if (impression != null) {
            for (String impr : impression) {
                System.out.println("|----|----|----" + impr);
            }
        }

        System.out.println("|----|----Clicks: ");
        if (click != null) {
            for (String c : click) {
                System.out.println("|----|----|----" + c);
            }
        }

        System.out.println("|----|----Downloads: ");
        if (download != null) {
            for (String d : download) {
                System.out.println("|----|----|----" + d);
            }
        }

        System.out.println("|----|----Play Starts: ");
        if (playstart != null) {
            for (String ps : playstart) {
                System.out.println("|----|----|----" + ps);
            }
        }
        System.out.println("|----|----Event: " + event);
    }

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public Effect getEffects() {
		return effects;
	}

	public void setEffects(Effect effects) {
		this.effects = effects;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public Media getMedia() {
		return media;
	}

	public void setMedia(Media media) {
		this.media = media;
	}

	public List<String> getImpression() {
		return impression;
	}

	public void setImpression(List<String> impression) {
		this.impression = impression;
	}

	public List<String> getClick() {
		return click;
	}

	public void setClick(List<String> click) {
		this.click = click;
	}

	public List<String> getDownload() {
		return download;
	}

	public void setDownload(List<String> download) {
		this.download = download;
	}

	public List<String> getPlaystart() {
		return playstart;
	}

	public void setPlaystart(List<String> playstart) {
		this.playstart = playstart;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}




}
