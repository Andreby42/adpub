package com.bus.chelaile.koubei;

/**
 * Created by zhaoling on 2018/1/17.
 */
public class CouponUseInfo {
    private String partnerId;
    private String benefitId;

    public CouponUseInfo(String partnerId, String benefitId) {
        this.partnerId = partnerId;
        this.benefitId = benefitId;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getBenefitId() {
        return benefitId;
    }

    public void setBenefitId(String benefitId) {
        this.benefitId = benefitId;
    }
}
