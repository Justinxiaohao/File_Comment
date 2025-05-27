package com.example.mapper;

import com.example.entity.Seat;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 座位数据操作接口
 */
public interface SeatMapper {

    void insert(Seat seat);

    void updateById(Seat seat);

    void deleteById(Integer id);

    @Select("select * from seat where id = #{id}")
    Seat selectById(Integer id);

    List<Seat> selectAll(Seat seat);

    @Select("select * from seat where hall_id = #{hallId}")
    List<Seat> selectByHallId(Integer hallId);

    @Select("select * from seat where id in (${seatIds})")
    List<Seat> selectByIds(String seatIds);
}
