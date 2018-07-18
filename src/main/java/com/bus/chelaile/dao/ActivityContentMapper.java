package com.bus.chelaile.dao;

import java.util.List;

import com.bus.chelaile.flow.model.ActivityContent;
import com.bus.chelaile.flow.model.FlowChannel;


public interface ActivityContentMapper {
    List<ActivityContent> listValidActivity();
    List<FlowChannel> listValidChannel();
    List<ActivityContent> listOnlineActivity();
    List<ActivityContent> listTabActivity();
}
