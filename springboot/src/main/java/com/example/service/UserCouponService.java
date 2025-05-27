package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.entity.Coupon;
import com.example.entity.User;
import com.example.entity.UserCoupon;
import com.example.exception.CustomException;
import com.example.mapper.UserCouponMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 用户优惠券业务处理
 */
@Service
public class UserCouponService {

    @Resource
    private UserCouponMapper userCouponMapper;
    @Resource
    private UserService userService;
    @Resource
    private CouponService couponService;

    /**
     * 新增
     */
    public void add(UserCoupon userCoupon) {
        userCouponMapper.insert(userCoupon);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        userCouponMapper.deleteById(id);
    }

    /**
     * 修改
     */
    public void update(UserCoupon userCoupon) {
        userCouponMapper.updateById(userCoupon);
    }

    /**
     * 根据ID查询
     */
    public UserCoupon selectById(Integer id) {
        return userCouponMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<UserCoupon> selectAll(UserCoupon userCoupon) {
        return userCouponMapper.selectAll(userCoupon);
    }

    /**
     * 分页查询
     */
    public PageInfo<UserCoupon> selectPage(UserCoupon userCoupon, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<UserCoupon> list = userCouponMapper.selectAll(userCoupon);
        return PageInfo.of(list);
    }

    /**
     * 查询用户未使用的优惠券
     */
    public List<UserCoupon> selectUnusedByUserId(Integer userId) {
        return userCouponMapper.selectUnusedByUserId(userId);
    }

    /**
     * 根据优惠券ID查询用户未使用的优惠券
     */
    public UserCoupon selectUnusedByCouponId(Integer userId, Integer couponId) {
        return userCouponMapper.selectUnusedByCouponId(userId, couponId);
    }

    /**
     * 领取优惠券
     */
    @Transactional
    public void receiveCoupon(Integer userId, Integer couponId) {
        // 检查用户是否存在
        User user = userService.selectById(userId);
        if (ObjectUtil.isNull(user)) {
            throw new CustomException("用户不存在");
        }

        // 检查优惠券是否存在
        Coupon coupon = couponService.selectById(couponId);
        if (ObjectUtil.isNull(coupon)) {
            throw new CustomException("优惠券不存在");
        }

        // 检查优惠券是否有效
        if (coupon.getStatus() != 1) {
            throw new CustomException("优惠券已失效");
        }

        // 检查优惠券是否在有效期内
        Date now = new Date();
        if (now.before(coupon.getStartDate()) || now.after(coupon.getEndDate())) {
            throw new CustomException("优惠券不在有效期内");
        }

        // 检查用户是否已领取过该优惠券（包括已使用的）
        UserCoupon existUserCoupon = userCouponMapper.selectByCouponId(userId, couponId);
        if (ObjectUtil.isNotNull(existUserCoupon)) {
            throw new CustomException("您已领取过该优惠券，每位用户只能领取一次");
        }

        // 创建用户优惠券记录
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(couponId);
        userCoupon.setIsUsed(0);
        userCoupon.setGetTime(now);
        userCouponMapper.insert(userCoupon);
    }
}
