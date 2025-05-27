package com.example.controller;

import com.example.common.Result;
import com.example.entity.Coupon;
import com.example.service.CouponService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 优惠券前端操作接口
 */
@RestController
@RequestMapping("/coupon")
public class CouponController {

    @Resource
    private CouponService couponService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Coupon coupon) {
        couponService.add(coupon);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        couponService.deleteById(id);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result update(@RequestBody Coupon coupon) {
        couponService.update(coupon);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Coupon coupon = couponService.selectById(id);
        return Result.success(coupon);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Coupon coupon) {
        List<Coupon> list = couponService.selectAll(coupon);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Coupon coupon,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Coupon> page = couponService.selectPage(coupon, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 查询节日优惠券
     */
    @GetMapping("/selectHolidayCoupons")
    public Result selectHolidayCoupons() {
        List<Coupon> list = couponService.selectHolidayCoupons();
        return Result.success(list);
    }

    /**
     * 查询有效优惠券
     */
    @GetMapping("/selectValidCoupons")
    public Result selectValidCoupons() {
        List<Coupon> list = couponService.selectValidCoupons();
        return Result.success(list);
    }
}
