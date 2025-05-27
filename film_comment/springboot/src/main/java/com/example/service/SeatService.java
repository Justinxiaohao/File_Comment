package com.example.service;

import com.example.entity.Seat;
import com.example.mapper.SeatMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 座位业务处理
 */
@Service
public class SeatService {

    @Resource
    private SeatMapper seatMapper;

    /**
     * 新增
     */
    public void add(Seat seat) {
        seatMapper.insert(seat);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        seatMapper.deleteById(id);
    }

    /**
     * 修改
     */
    public void update(Seat seat) {
        seatMapper.updateById(seat);
    }

    /**
     * 根据ID查询
     */
    public Seat selectById(Integer id) {
        return seatMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Seat> selectAll(Seat seat) {
        return seatMapper.selectAll(seat);
    }

    /**
     * 分页查询
     */
    public PageInfo<Seat> selectPage(Seat seat, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Seat> list = seatMapper.selectAll(seat);
        return PageInfo.of(list);
    }

    /**
     * 根据影厅ID查询座位
     */
    public List<Seat> selectByHallId(Integer hallId) {
        return seatMapper.selectByHallId(hallId);
    }

    /**
     * 根据座位ID列表查询座位
     */
    public List<Seat> selectByIds(String seatIds) {
        return seatMapper.selectByIds(seatIds);
    }
}
