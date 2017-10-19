package com.bus.chelaile.model;


public enum PushType {
    FEED("feed"),
    FEEDBACK("feedback");


    PushType(String type) {
        this.type = type;
    }

    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
