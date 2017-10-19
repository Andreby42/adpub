package com.bus.chelaile.push.pushModel;

import com.bus.chelaile.model.Platform;

/**
 * Created by Administrator on 2016/4/11 0011.
 */
public enum TokenType {
    GTTOKEN("0", Platform.GT),
    YMTOKEN("1", Platform.YM);

    private String name;
    private Platform platform;


    TokenType(String name, Platform platform) {
        this.name = name;
        this.platform = platform;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }
}
