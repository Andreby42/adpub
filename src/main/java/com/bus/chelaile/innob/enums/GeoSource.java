package com.bus.chelaile.innob.enums;

/**
 * Created by Administrator on 2016/8/8.
 */
public enum GeoSource {
    GPS, IP;

    @Override
    public String toString() {
        return Integer.toString(this.ordinal());
    }
}