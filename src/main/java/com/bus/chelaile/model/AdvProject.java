package com.bus.chelaile.model;

import lombok.Data;


@Data
public class AdvProject {

    private String projectId;
    private int projectTotalSend;
    private int projectTotalClick;
    private int projectDaySend;
    private int projectDayClick;
    private int projectClick;
    private int projectIdClickExpireTime;
    
    
}
