package com.bus.chelaile.koubei;

/**
 * Created by zhaoling on 2018/1/10.
 */
public class CouponQueryParam {
    private String channel = "chelaile";
    private String longitude;
    private String latitude;
    private String city_code;
    private String user_id;

    public CouponQueryParam(String city_code) {
        this.city_code = city_code;
    }

    public CouponQueryParam(String longitude, String latitude, String city_code) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.city_code = city_code;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getCity_code() {
        return city_code;
    }

    public void setCity_code(String city_code) {
        this.city_code = city_code;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
