package com.bus.chelaile.koubei;

/**
 * Created by zhaoling on 2018/1/16.
 */
public class KBUpdateCouponStatusThread implements Runnable{

    private CouponService couponService;

    public KBUpdateCouponStatusThread(CouponService couponService) {
        this.couponService = couponService;
    }

    @Override
	public void run() {
		try {
			couponService.updateCouponStatus();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    public CouponService getCouponService() {
        return couponService;
    }

    public void setCouponService(CouponService couponService) {
        this.couponService = couponService;
    }
}
