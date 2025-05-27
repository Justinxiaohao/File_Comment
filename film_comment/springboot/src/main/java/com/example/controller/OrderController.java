package com.example.controller;

import com.example.common.Result;
import com.example.entity.Order;
import com.example.service.OrderService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单前端操作接口
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Resource
    private OrderService orderService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Order order) {
        Order newOrder = orderService.add(order);
        return Result.success(newOrder);
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        orderService.deleteById(id);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result update(@RequestBody Order order) {
        orderService.update(order);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Order order = orderService.selectById(id);
        return Result.success(order);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Order order) {
        List<Order> list = orderService.selectAll(order);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Order order,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Order> page = orderService.selectPage(order, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 根据用户ID查询订单
     */
    @GetMapping("/selectByUserId/{userId}")
    public Result selectByUserId(@PathVariable Integer userId) {
        List<Order> list = orderService.selectByUserId(userId);
        return Result.success(list);
    }

    /**
     * 根据订单号查询订单
     */
    @GetMapping("/selectByOrderNo/{orderNo}")
    public Result selectByOrderNo(@PathVariable String orderNo) {
        Order order = orderService.selectByOrderNo(orderNo);
        return Result.success(order);
    }

    /**
     * 支付订单
     */
    @PostMapping("/pay/{orderNo}")
    public Result pay(@PathVariable String orderNo) {
        orderService.pay(orderNo);
        return Result.success();
    }

    /**
     * 取消订单
     */
    @PostMapping("/cancel/{orderNo}")
    public Result cancel(@PathVariable String orderNo) {
        orderService.cancel(orderNo);
        return Result.success();
    }
}
