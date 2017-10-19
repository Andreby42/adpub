package com.bus.chelaile.innob.enums;

/**
 * Created by Administrator on 2016/8/8.
 */
public enum ConnType {
    ETHERNET, WIFI, UNKNOWN, G2, G3, G4;

    @Override
    public String toString() {
        return Integer.toString(this.ordinal());
    }
}
