package com.bus.chelaile.model.ads;

import lombok.Data;


@Data
public class AdvProject {

    private int projectId;
    private String projectName;
    private int projectTotalSend;
    private int projectTotalClick;
    private int projectDaySend;
    private int projectDayClick;
    private int projectClick;
    private int projectIdClickExpireTime;
    
    
}
