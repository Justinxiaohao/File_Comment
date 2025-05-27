package com.example.controller;

import com.example.common.Result;
import com.example.entity.UserCoupon;
import com.example.service.UserCouponService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户优惠券前端操作接口
 */
@RestController
@RequestMapping("/userCoupon")
public class UserCouponController {

    @Resource
    private UserCouponService userCouponService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody UserCoupon userCoupon) {
        userCouponService.add(userCoupon);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        userCouponService.deleteById(id);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result update(@RequestBody UserCoupon userCoupon) {
        userCouponService.update(userCoupon);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        UserCoupon userCoupon = userCouponService.selectById(id);
        return Result.success(userCoupon);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(UserCoupon userCoupon) {
        List<UserCoupon> list = userCouponService.selectAll(userCoupon);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(UserCoupon userCoupon,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<UserCoupon> page = userCouponService.selectPage(userCoupon, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 查询用户未使用的优惠券
     */
    @GetMapping("/selectUnusedByUserId/{userId}")
    public Result selectUnusedByUserId(@PathVariable Integer userId) {
        List<UserCoupon> list = userCouponService.selectUnusedByUserId(userId);
        return Result.success(list);
    }

    /**
     * 领取优惠券
     */
    @PostMapping("/receive/{userId}/{couponId}")
    public Result receiveCoupon(@PathVariable Integer userId, @PathVariable Integer couponId) {
        userCouponService.receiveCoupon(userId, couponId);
        return Result.success();
    }
}
