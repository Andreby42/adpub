package com.bus.chelaile.koubei;

import java.util.Date;

/**
 * Created by zhaoling on 2018/1/12.
 */
public class CouponOrder {
    private long id;
    private String accountId;
    private String aliUserId;
    private String couponId;
    private String benefitId;
    private String partnerId;
    private String itemName;
    private String condition;
    private String shopName;
    private String imageUrl;
    private Date createTime;
    private Date updateTime;
    private int status;

    public CouponOrder() {
    }

    public CouponOrder(long id, int status) {
        this.id = id;
        this.status = status;
    }

    public CouponOrder(String accountId, String aliUserId, String couponId
            , String benefitId, String partnerId) {
        this.accountId = accountId;
        this.aliUserId = aliUserId;
        this.couponId = couponId;
        this.benefitId = benefitId;
        this.partnerId = partnerId;
    }

    @Override
    public String toString() {
        return "CouponOrder{" +
                "id=" + id +
                ", accountId='" + accountId + '\'' +
                ", aliUserId='" + aliUserId + '\'' +
                ", couponId='" + couponId + '\'' +
                ", benefitId='" + benefitId + '\'' +
                ", partnerId='" + partnerId + '\'' +
                ", itemName='" + itemName + '\'' +
                ", createTime=" + createTime +
                ", status=" + status +
                '}';
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAliUserId() {
        return aliUserId;
    }

    public void setAliUserId(String aliUserId) {
        this.aliUserId = aliUserId;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getBenefitId() {
        return benefitId;
    }

    public void setBenefitId(String benefitId) {
        this.benefitId = benefitId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
