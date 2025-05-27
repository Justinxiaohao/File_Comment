package com.example.service;

import com.example.entity.Screening;
import com.example.mapper.ScreeningMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 放映场次业务处理
 */
@Service
public class ScreeningService {

    @Resource
    private ScreeningMapper screeningMapper;

    /**
     * 新增
     */
    public void add(Screening screening) {
        screeningMapper.insert(screening);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        screeningMapper.deleteById(id);
    }

    /**
     * 修改
     */
    public void update(Screening screening) {
        screeningMapper.updateById(screening);
    }

    /**
     * 根据ID查询
     */
    public Screening selectById(Integer id) {
        return screeningMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Screening> selectAll(Screening screening) {
        return screeningMapper.selectAll(screening);
    }

    /**
     * 分页查询
     */
    public PageInfo<Screening> selectPage(Screening screening, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Screening> list = screeningMapper.selectAll(screening);
        return PageInfo.of(list);
    }

    /**
     * 根据电影ID查询场次
     */
    public List<Screening> selectByFilmId(Integer filmId) {
        return screeningMapper.selectByFilmId(filmId);
    }
}
