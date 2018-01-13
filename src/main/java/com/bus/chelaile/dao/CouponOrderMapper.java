package com.bus.chelaile.dao;

import com.bus.chelaile.koubei.CouponOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by zhaoling on 2018/1/12.
 */
public interface CouponOrderMapper {
    int insertCouponOrder(@Param("order") CouponOrder order);
    List<CouponOrder> listCouponOrders();
    void updateStatus(@Param("order") CouponOrder order);
}
