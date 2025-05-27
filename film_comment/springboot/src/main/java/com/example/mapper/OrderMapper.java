package com.example.mapper;

import com.example.entity.Order;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 订单数据操作接口
 */
public interface OrderMapper {

    void insert(Order order);

    void updateById(Order order);

    void deleteById(Integer id);

    @Select("select * from `order` where id = #{id}")
    Order selectById(Integer id);

    List<Order> selectAll(Order order);

    @Select("select * from `order` where user_id = #{userId}")
    List<Order> selectByUserId(Integer userId);

    @Select("select * from `order` where order_no = #{orderNo}")
    Order selectByOrderNo(String orderNo);
}
