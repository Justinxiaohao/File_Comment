package com.example.mapper;

import com.example.entity.Screening;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 放映场次数据操作接口
 */
public interface ScreeningMapper {

    void insert(Screening screening);

    void updateById(Screening screening);

    void deleteById(Integer id);

    @Select("select * from screening where id = #{id}")
    Screening selectById(Integer id);

    List<Screening> selectAll(Screening screening);

    @Select("select * from screening where film_id = #{filmId}")
    List<Screening> selectByFilmId(Integer filmId);
}
