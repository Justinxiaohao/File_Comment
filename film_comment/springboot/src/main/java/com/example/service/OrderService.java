package com.example.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.entity.Coupon;
import com.example.entity.Order;
import com.example.entity.Screening;
import com.example.entity.User;
import com.example.entity.UserCoupon;
import com.example.exception.CustomException;
import com.example.mapper.OrderMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单业务处理
 */
@Service
public class OrderService {

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private ScreeningService screeningService;
    @Resource
    private UserService userService;
    @Resource
    private CouponService couponService;
    @Resource
    private UserCouponService userCouponService;

    /**
     * 新增
     */
    @Transactional
    public Order add(Order order) {
        // 生成订单号
        order.setOrderNo(IdUtil.getSnowflakeNextIdStr());
        // 设置订单状态为未支付
        order.setStatus(0);
        // 设置创建时间
        order.setCreateTime(new Date());

        // 获取场次信息
        Screening screening = screeningService.selectById(order.getScreeningId());
        if (ObjectUtil.isNull(screening)) {
            throw new CustomException("场次不存在");
        }

        // 计算总价
        String[] seatIds = order.getSeatIds().split(",");
        BigDecimal totalPrice = screening.getPrice().multiply(new BigDecimal(seatIds.length));
        order.setTotalPrice(totalPrice);

        // 计算折扣金额和实际支付金额
        BigDecimal discountPrice = BigDecimal.ZERO;

        // 获取用户信息
        User user = userService.selectById(order.getUserId());

        // 计算会员等级折扣和VIP折上折
        BigDecimal levelDiscount = BigDecimal.ONE; // 默认无折扣
        BigDecimal vipExtraDiscount = BigDecimal.ONE; // 默认无额外VIP折扣
        BigDecimal priceAfterDiscount = totalPrice;

        if (ObjectUtil.isNotNull(user) && ObjectUtil.isNotNull(user.getVipLevel())) {
            // 根据会员等级设置折扣
            Integer vipLevel = user.getVipLevel();
            if (vipLevel != null && vipLevel > 0) {
                switch (vipLevel) {
                    case 1: // 青铜会员
                        levelDiscount = new BigDecimal("0.95");
                        break;
                    case 2: // 白银会员
                        levelDiscount = new BigDecimal("0.9");
                        break;
                    case 3: // 黄金会员
                        levelDiscount = new BigDecimal("0.85");
                        break;
                    case 4: // 钻石会员
                        levelDiscount = new BigDecimal("0.8");
                        break;
                    case 5: // 至尊会员
                        levelDiscount = new BigDecimal("0.75");
                        break;
                    default:
                        levelDiscount = BigDecimal.ONE;
                }

                // 应用会员等级折扣
                if (levelDiscount.compareTo(BigDecimal.ONE) < 0) {
                    BigDecimal levelDiscountAmount = totalPrice.multiply(BigDecimal.ONE.subtract(levelDiscount));
                    discountPrice = discountPrice.add(levelDiscountAmount);
                    priceAfterDiscount = totalPrice.multiply(levelDiscount);
                }

                // 如果是VIP会员，再额外打95折（折上折）
                if (ObjectUtil.isNotNull(user.getIsVip()) && user.getIsVip() == 1) {
                    vipExtraDiscount = new BigDecimal("0.95");
                    BigDecimal vipExtraDiscountAmount = priceAfterDiscount.multiply(BigDecimal.ONE.subtract(vipExtraDiscount));
                    discountPrice = discountPrice.add(vipExtraDiscountAmount);
                    priceAfterDiscount = priceAfterDiscount.multiply(vipExtraDiscount);
                }
            }
        }

        // 如果使用了优惠券
        if (ObjectUtil.isNotNull(order.getCouponId()) && order.getCouponId() > 0) {
            // 获取优惠券信息
            Coupon coupon = couponService.selectById(order.getCouponId());
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

            // 检查用户是否拥有该优惠券
            UserCoupon userCoupon = userCouponService.selectUnusedByCouponId(order.getUserId(), order.getCouponId());
            if (ObjectUtil.isNull(userCoupon)) {
                throw new CustomException("您没有该优惠券或已使用");
            }

            // 计算优惠券折扣金额（在会员等级和VIP折扣基础上再打折）
            BigDecimal couponDiscountAmount = priceAfterDiscount.multiply(BigDecimal.ONE.subtract(coupon.getDiscount()));
            discountPrice = discountPrice.add(couponDiscountAmount);
            priceAfterDiscount = priceAfterDiscount.multiply(coupon.getDiscount());

            // 标记优惠券为已使用
            userCoupon.setIsUsed(1);
            userCoupon.setUseTime(new Date());
            userCouponService.update(userCoupon);
        }

        order.setDiscountPrice(discountPrice);
        order.setActualPrice(totalPrice.subtract(discountPrice));

        orderMapper.insert(order);
        return order;
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        orderMapper.deleteById(id);
    }

    /**
     * 修改
     */
    public void update(Order order) {
        orderMapper.updateById(order);
    }

    /**
     * 根据ID查询
     */
    public Order selectById(Integer id) {
        return orderMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Order> selectAll(Order order) {
        return orderMapper.selectAll(order);
    }

    /**
     * 分页查询
     */
    public PageInfo<Order> selectPage(Order order, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> list = orderMapper.selectAll(order);
        return PageInfo.of(list);
    }

    /**
     * 根据用户ID查询订单
     */
    public List<Order> selectByUserId(Integer userId) {
        return orderMapper.selectByUserId(userId);
    }

    /**
     * 根据订单号查询订单
     */
    public Order selectByOrderNo(String orderNo) {
        return orderMapper.selectByOrderNo(orderNo);
    }

    /**
     * 支付订单
     */
    @Transactional
    public void pay(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (ObjectUtil.isNull(order)) {
            throw new CustomException("订单不存在");
        }

        if (order.getStatus() != 0) {
            throw new CustomException("订单状态不正确");
        }

        // 更新订单状态为已支付
        order.setStatus(1);
        order.setPayTime(new Date());
        orderMapper.updateById(order);

        // 获取用户信息
        User user = userService.selectById(order.getUserId());
        if (ObjectUtil.isNotNull(user)) {
            // 计算积分，按照实际支付金额的10倍计算积分
            int earnedPoints = order.getActualPrice().multiply(new BigDecimal("10")).intValue();

            // 更新用户积分
            int currentPoints = user.getPoints() != null ? user.getPoints() : 0;
            int newPoints = currentPoints + earnedPoints;
            user.setPoints(newPoints);

            // 根据积分更新会员等级
            updateVipLevel(user, newPoints);

            // 保存用户信息
            userService.updateById(user);
        }
    }

    /**
     * 根据积分更新会员等级
     */
    private void updateVipLevel(User user, int points) {
        // 设置会员等级
        // 0-普通用户（0-999积分）
        // 1-青铜会员（1000-2999积分）
        // 2-白银会员（3000-5999积分）
        // 3-黄金会员（6000-9999积分）
        // 4-钻石会员（10000-19999积分）
        // 5-至尊会员（20000+积分）
        if (points >= 20000) {
            user.setVipLevel(5);
        } else if (points >= 10000) {
            user.setVipLevel(4);
        } else if (points >= 6000) {
            user.setVipLevel(3);
        } else if (points >= 3000) {
            user.setVipLevel(2);
        } else if (points >= 1000) {
            user.setVipLevel(1);
        } else {
            user.setVipLevel(0);
        }

        // 如果积分达到1000，自动设置为VIP用户
        if (points >= 1000) {
            user.setIsVip(1);
        }
    }

    /**
     * 取消订单
     */
    @Transactional
    public void cancel(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (ObjectUtil.isNull(order)) {
            throw new CustomException("订单不存在");
        }

        if (order.getStatus() != 0) {
            throw new CustomException("订单状态不正确");
        }

        // 如果使用了优惠券，需要退还
        if (ObjectUtil.isNotNull(order.getCouponId()) && order.getCouponId() > 0) {
            UserCoupon userCoupon = userCouponService.selectUnusedByCouponId(order.getUserId(), order.getCouponId());
            if (ObjectUtil.isNotNull(userCoupon)) {
                userCoupon.setIsUsed(0);
                userCoupon.setUseTime(null);
                userCouponService.update(userCoupon);
            }
        }

        // 更新订单状态为已取消
        order.setStatus(2);
        orderMapper.updateById(order);
    }
}
