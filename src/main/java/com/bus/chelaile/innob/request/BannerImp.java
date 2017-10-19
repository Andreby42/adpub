package com.bus.chelaile.innob.request;

/**
 * Created by Administrator on 2016/10/24.
 */
public class BannerImp extends Imp {
    private Banner banner = new Banner();
    private int secure  = 0;
    private Ext ext = new Ext();

    public Banner getBanner() {
        return banner;
    }

    public void setBanner(Banner banner) {
        this.banner = banner;
    }

    public int getSecure() {
        return secure;
    }

    public void setSecure(int secure) {
        this.secure = secure;
    }

    public Ext getExt() {
        return ext;
    }

    public void setExt(Ext ext) {
        this.ext = ext;
    }

    public class Banner {
        int w = 320;
        int h = 50;
        int[] api = {1, 2};

        public int getW() {
            return w;
        }

        public void setW(int w) {
            this.w = w;
        }

        public int getH() {
            return h;
        }

        public void setH(int h) {
            this.h = h;
        }

        public int[] getApi() {
            return api;
        }

        public void setApi(int[] api) {
            this.api = api;
        }
    }

    public class Ext {
        int ads = 1;

        public int getAds() {
            return ads;
        }

        public void setAds(int ads) {
            this.ads = ads;
        }
    }
}
