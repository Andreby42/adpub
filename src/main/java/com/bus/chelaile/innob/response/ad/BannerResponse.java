package com.bus.chelaile.innob.response.ad;


import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/9.
 */

public class BannerResponse extends AdResponse {
    private static class Ad {
        private String pubContent;
        private String adtype;
        private int width;
        private int height;

        public String getPubContent() {
            return pubContent;
        }

        public void setPubContent(String pubContent) {
            this.pubContent = pubContent;
        }

        public String getAdtype() {
            return adtype;
        }

        public void setAdtype(String adtype) {
            this.adtype = adtype;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }
    private ArrayList<Ad> ads;
    private String requestId;

    public ArrayList<Ad> getAds() {
        return ads;
    }

    public void setAds(ArrayList<Ad> ads) {
        this.ads = ads;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public void print() {
        System.out.println("pubContent: " + getPubContent());
        System.out.println("adType: " + getAdtype());
        System.out.println("width: " + getWidth());
        System.out.println("height: " + getHeight());
        System.out.println("Request Id: " + getRequestId());
    }

    public String getPubContent() {
        return ads.get(0).getPubContent();
    }

    public String getAdtype() {
        return ads.get(0).getAdtype();
    }

    public int getWidth() {
        return ads.get(0).getWidth();
    }

    public int getHeight() {
        return ads.get(0).getHeight();
    }
}
