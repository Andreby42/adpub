package com.bus.chelaile.innob.request;



/**
 * Created by Administrator on 2016/8/8.
 */

public class NativeImp extends Imp {
    private int secure = 0;
    private String trackertype = "url_ping";
    private Native Native = new Native();
    private Ext ext = new Ext();
    
    public NativeImp(int ads){
    	ext.setAds(ads);	//表明一次请求返回几个inmobi广告
    }

    public int getSecure() {
        return secure;
    }

    public void setSecure(int secure) {
        this.secure = secure;
    }

    public String getTrackertype() {
        return trackertype;
    }

    public void setTrackertype(String trackertype) {
        this.trackertype = trackertype;
    }

    public NativeImp.Native getNative() {
        return Native;
    }

    public void setNative(NativeImp.Native aNative) {
        Native = aNative;
    }

    public Ext getExt() {
        return ext;
    }

    public void setExt(Ext ext) {
        this.ext = ext;
    }

    public class Native {
        private int layout = 0;

        public int getLayout() {
            return layout;
        }
    }

    public class Ext {
        private int ads = 5;

        public int getAds() {
            return ads;
        }

        public void setAds(int ads) {
            this.ads = ads;
        }
    }
}
