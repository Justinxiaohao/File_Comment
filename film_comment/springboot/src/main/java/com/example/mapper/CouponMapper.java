package com.example.mapper;

import com.example.entity.Coupon;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 优惠券数据操作接口
 */
public interface CouponMapper {

    void insert(Coupon coupon);

    void updateById(Coupon coupon);

    void deleteById(Integer id);

    @Select("select * from coupon where id = #{id}")
    Coupon selectById(Integer id);

    List<Coupon> selectAll(Coupon coupon);

    @Select("select * from coupon where is_holiday = 1 and status = 1")
    List<Coupon> selectHolidayCoupons();

    @Select("select * from coupon where status = 1")
    List<Coupon> selectValidCoupons();
}
