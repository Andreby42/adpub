package com.bus.chelaile.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.bus.chelaile.model.ads.AdContent;



public interface AppAdvContentMapper {
   // List<AdContent> list();
    List<AdContent> listValidAds();
    AdContent query4Id(@Param("id") int id);
}
