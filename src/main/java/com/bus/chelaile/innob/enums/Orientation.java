package com.bus.chelaile.innob.enums;

/**
 * Created by Administrator on 2016/8/8.
 */
public enum Orientation {
    VERTICAL, REVERSED_VERTICAL, HORIZONAL, REVERSED_HORIZONAL;

    @Override
    public String toString() {
        return Integer.toString(this.ordinal());
    }
}
