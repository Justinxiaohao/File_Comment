package com.example.entity;

import java.util.Date;

/**
 * 用户优惠券
 */
public class UserCoupon {
    /** ID */
    private Integer id;
    /** 用户ID */
    private Integer userId;
    /** 优惠券ID */
    private Integer couponId;
    /** 是否已使用：0-未使用，1-已使用 */
    private Integer isUsed;
    /** 获取时间 */
    private Date getTime;
    /** 使用时间 */
    private Date useTime;
    /** 用户名称 */
    private String userName;
    /** 优惠券名称 */
    private String couponName;
    /** 折扣率 */
    private String discount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getCouponId() {
        return couponId;
    }

    public void setCouponId(Integer couponId) {
        this.couponId = couponId;
    }

    public Integer getIsUsed() {
        return isUsed;
    }

    public void setIsUsed(Integer isUsed) {
        this.isUsed = isUsed;
    }

    public Date getGetTime() {
        return getTime;
    }

    public void setGetTime(Date getTime) {
        this.getTime = getTime;
    }

    public Date getUseTime() {
        return useTime;
    }

    public void setUseTime(Date useTime) {
        this.useTime = useTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }
}
