package com.bus.chelaile.dao;

import com.bus.chelaile.model.strategy.AdStrategyParam;

import java.util.List;

/**
 * Created by tingx on 2016/11/28.
 */
public interface AppAdvStrategyMapper {
    List<AdStrategyParam> listDefaultAdvStrategies();
    List<AdStrategyParam> listSpecifiedAdvStrategies();
}
