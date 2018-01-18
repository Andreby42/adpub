package com.bus.chelaile.koubei;

/**
 * Created by zhaoling on 2018/1/10.
 */
public class CouponInfo {
    private String itemId;
    private String itemName;
    private String distance;
    private String condition;
    private String shopName;
    private String imageUrl;
    private String partnerId;
    private String benefitId;
    // 0(未领取),1(可用),2(已使用),3(已过期),4(已关闭),5(已冻结)
    private int status;

    public CouponInfo() {
    }

    public CouponInfo(String itemId, String itemName, String distance, String condition, String shopName, String imageUrl) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.distance = distance;
        this.condition = condition;
        this.shopName = shopName;
        this.imageUrl = imageUrl;
    }

    public CouponInfo(String itemId, String distance, String shopName, String imageUrl) {
        this.itemId = itemId;
        this.distance = distance;
        this.shopName = shopName;
        this.imageUrl = imageUrl;
    }

    public String getBenefitId() {
        return benefitId;
    }

    public void setBenefitId(String benefitId) {
        this.benefitId = benefitId;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    @Override
    public String toString() {
        return "CouponInfo{" +
                "itemId='" + itemId + '\'' +
                ", itemName='" + itemName + '\'' +
                ", distance='" + distance + '\'' +
                ", condition='" + condition + '\'' +
                ", shopName='" + shopName + '\'' +
                ", status=" + status +
                '}';
    }
}
