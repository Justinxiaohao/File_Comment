package com.example.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单信息
 */
public class Order {
    /** ID */
    private Integer id;
    /** 订单号 */
    private String orderNo;
    /** 用户ID */
    private Integer userId;
    /** 场次ID */
    private Integer screeningId;
    /** 座位ID，多个用逗号分隔 */
    private String seatIds;
    /** 总价 */
    private BigDecimal totalPrice;
    /** 折扣金额 */
    private BigDecimal discountPrice;
    /** 实际支付金额 */
    private BigDecimal actualPrice;
    /** 状态：0-未支付，1-已支付，2-已取消 */
    private Integer status;
    /** 创建时间 */
    private Date createTime;
    /** 支付时间 */
    private Date payTime;
    /** 优惠券ID */
    private Integer couponId;
    /** 用户名称 */
    private String userName;
    /** 电影名称 */
    private String filmName;
    /** 场次信息 */
    private String screeningInfo;
    /** 座位信息 */
    private String seatInfo;
    /** 优惠券名称 */
    private String couponName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getScreeningId() {
        return screeningId;
    }

    public void setScreeningId(Integer screeningId) {
        this.screeningId = screeningId;
    }

    public String getSeatIds() {
        return seatIds;
    }

    public void setSeatIds(String seatIds) {
        this.seatIds = seatIds;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(BigDecimal discountPrice) {
        this.discountPrice = discountPrice;
    }

    public BigDecimal getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(BigDecimal actualPrice) {
        this.actualPrice = actualPrice;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public Integer getCouponId() {
        return couponId;
    }

    public void setCouponId(Integer couponId) {
        this.couponId = couponId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFilmName() {
        return filmName;
    }

    public void setFilmName(String filmName) {
        this.filmName = filmName;
    }

    public String getScreeningInfo() {
        return screeningInfo;
    }

    public void setScreeningInfo(String screeningInfo) {
        this.screeningInfo = screeningInfo;
    }

    public String getSeatInfo() {
        return seatInfo;
    }

    public void setSeatInfo(String seatInfo) {
        this.seatInfo = seatInfo;
    }

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }
}
