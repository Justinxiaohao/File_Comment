package com.example.mapper;

import com.example.entity.Hall;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 影厅数据操作接口
 */
public interface HallMapper {

    void insert(Hall hall);

    void updateById(Hall hall);

    void deleteById(Integer id);

    @Select("select * from hall where id = #{id}")
    Hall selectById(Integer id);

    List<Hall> selectAll(Hall hall);
}
