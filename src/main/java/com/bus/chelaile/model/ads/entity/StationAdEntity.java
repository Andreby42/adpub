package com.bus.chelaile.model.ads.entity;

import com.bus.chelaile.model.ShowType;


public class StationAdEntity extends BaseAdEntity {

	public StationAdEntity() {
        super(ShowType.STATION_ADV.getValue());
//        this.pic = EMPTY_STR;
//        this.combpic = EMPTY_STR;
	}

	@Override
	protected ShowType gainShowTypeEnum() {
		// TODO Auto-generated method stub
		return null;
	}

}
