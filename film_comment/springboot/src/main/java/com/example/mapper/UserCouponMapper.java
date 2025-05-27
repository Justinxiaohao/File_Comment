package com.example.mapper;

import com.example.entity.UserCoupon;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户优惠券数据操作接口
 */
public interface UserCouponMapper {

    void insert(UserCoupon userCoupon);

    void updateById(UserCoupon userCoupon);

    void deleteById(Integer id);

    @Select("select * from user_coupon where id = #{id}")
    UserCoupon selectById(Integer id);

    List<UserCoupon> selectAll(UserCoupon userCoupon);

    @Select("select uc.*, c.name as couponName, c.discount as discount from user_coupon uc " +
            "left join coupon c on uc.coupon_id = c.id " +
            "where uc.user_id = #{userId} and uc.is_used = 0")
    List<UserCoupon> selectUnusedByUserId(Integer userId);

    @Select("select * from user_coupon where user_id = #{userId} and coupon_id = #{couponId} and is_used = 0")
    UserCoupon selectUnusedByCouponId(Integer userId, Integer couponId);

    @Select("select * from user_coupon where user_id = #{userId} and coupon_id = #{couponId}")
    UserCoupon selectByCouponId(Integer userId, Integer couponId);
}
