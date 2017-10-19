package com.bus.chelaile.innob.request;

import com.alibaba.fastjson.annotation.JSONField;
import com.bus.chelaile.innob.utils.Validator;

/**
 * Created by Administrator on 2016/10/24.
 */
public abstract class Imp implements Validator {
    @JSONField(serialize = false)
    public boolean isValid() {
        return true;
    }
}
