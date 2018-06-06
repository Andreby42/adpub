package com.bus.chelaile.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bus.chelaile.model.QueryParam;
import com.bus.chelaile.model.ShowType;
import com.bus.chelaile.model.ads.entity.BaseAdEntity;
import com.bus.chelaile.model.ads.entity.TasksGroup;
import com.bus.chelaile.mvc.AdvParam;
import com.bus.chelaile.service.impl.OpenManager;

public class JSService {

    @Autowired
    private ServiceManager serviceManager;
    @Autowired
    private OpenManager openManager;

    protected static final Logger logger = LoggerFactory.getLogger(JSService.class);

    public TasksGroup getTask(AdvParam param) {

        List<BaseAdEntity> entities = openManager.doServiceList(param, ShowType.OPEN_SCREEN, new QueryParam());
        if (entities != null && entities.size() > 0) {
            for (BaseAdEntity entity : entities) {
                if(entity.getTasksGroup() != null) {
                    return entity.getTasksGroup();
                }
            }
        }

        return null;
    }

}
