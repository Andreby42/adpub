package com.bus.chelaile.dao;


import org.apache.ibatis.annotations.Param;

import com.bus.chelaile.model.rule.AdRule;

import java.util.Date;
import java.util.List;

public interface AppAdvRuleMapper {
    
    List<AdRule> list4AdvIdByTime(@Param("advId") int advId, @Param("today") Date today);


}
