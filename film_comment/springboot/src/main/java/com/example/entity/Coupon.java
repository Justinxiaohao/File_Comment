package com.example.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 优惠券信息
 */
public class Coupon {
    /** ID */
    private Integer id;
    /** 优惠券名称 */
    private String name;
    /** 折扣率，如0.8表示8折 */
    private BigDecimal discount;
    /** 开始日期 */
    private Date startDate;
    /** 结束日期 */
    private Date endDate;
    /** 是否节日优惠券：0-否，1-是 */
    private Integer isHoliday;
    /** 状态：0-无效，1-有效 */
    private Integer status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getIsHoliday() {
        return isHoliday;
    }

    public void setIsHoliday(Integer isHoliday) {
        this.isHoliday = isHoliday;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
