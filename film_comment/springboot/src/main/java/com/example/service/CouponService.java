package com.example.service;

import com.example.entity.Coupon;
import com.example.mapper.CouponMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 优惠券业务处理
 */
@Service
public class CouponService {

    @Resource
    private CouponMapper couponMapper;

    /**
     * 新增
     */
    public void add(Coupon coupon) {
        couponMapper.insert(coupon);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        couponMapper.deleteById(id);
    }

    /**
     * 修改
     */
    public void update(Coupon coupon) {
        couponMapper.updateById(coupon);
    }

    /**
     * 根据ID查询
     */
    public Coupon selectById(Integer id) {
        return couponMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Coupon> selectAll(Coupon coupon) {
        return couponMapper.selectAll(coupon);
    }

    /**
     * 分页查询
     */
    public PageInfo<Coupon> selectPage(Coupon coupon, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Coupon> list = couponMapper.selectAll(coupon);
        return PageInfo.of(list);
    }

    /**
     * 查询节日优惠券
     */
    public List<Coupon> selectHolidayCoupons() {
        return couponMapper.selectHolidayCoupons();
    }

    /**
     * 查询有效优惠券
     */
    public List<Coupon> selectValidCoupons() {
        return couponMapper.selectValidCoupons();
    }
}
