package com.example.service;

import com.example.entity.Hall;
import com.example.entity.Seat;
import com.example.mapper.HallMapper;
import com.example.mapper.SeatMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 影厅业务处理
 */
@Service
public class HallService {

    @Resource
    private HallMapper hallMapper;

    @Resource
    private SeatMapper seatMapper;

    /**
     * 新增
     */
    @Transactional
    public void add(Hall hall) {
        hallMapper.insert(hall);
        // 自动创建座位
        createSeats(hall);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        hallMapper.deleteById(id);
    }

    /**
     * 修改
     */
    @Transactional
    public void update(Hall hall) {
        // 获取原影厅信息
        Hall oldHall = hallMapper.selectById(hall.getId());

        // 更新影厅信息
        hallMapper.updateById(hall);

        // 如果行数或列数发生变化，则更新座位
        if (oldHall != null && (oldHall.getRowCount() != hall.getRowCount() ||
                oldHall.getColumnCount() != hall.getColumnCount())) {
            updateSeats(hall, oldHall);
        }
    }

    /**
     * 创建座位
     */
    private void createSeats(Hall hall) {
        List<Seat> seats = new ArrayList<>();

        // 为每一个行列位置创建座位
        for (int row = 1; row <= hall.getRowCount(); row++) {
            for (int col = 1; col <= hall.getColumnCount(); col++) {
                Seat seat = new Seat();
                seat.setRow(row);
                seat.setColumn(col);
                seat.setHallId(hall.getId());
                seat.setStatus(1); // 默认可用
                seats.add(seat);
            }
        }

        // 批量插入座位
        for (Seat seat : seats) {
            seatMapper.insert(seat);
        }
    }

    /**
     * 更新座位
     */
    private void updateSeats(Hall newHall, Hall oldHall) {
        // 获取当前影厅的所有座位
        List<Seat> existingSeats = seatMapper.selectByHallId(newHall.getId());

        // 创建新的座位（如果行列数增加）
        for (int row = 1; row <= newHall.getRowCount(); row++) {
            for (int col = 1; col <= newHall.getColumnCount(); col++) {
                // 检查该位置是否已有座位
                int finalCol = col;
                int finalRow = row;
                boolean seatExists = existingSeats.stream()
                        .anyMatch(s -> s.getRow() == finalRow && s.getColumn() == finalCol);

                // 如果没有座位，则创建新座位
                if (!seatExists) {
                    Seat seat = new Seat();
                    seat.setRow(row);
                    seat.setColumn(col);
                    seat.setHallId(newHall.getId());
                    seat.setStatus(1); // 默认可用
                    seatMapper.insert(seat);
                }
            }
        }
    }

    /**
     * 根据ID查询
     */
    public Hall selectById(Integer id) {
        return hallMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Hall> selectAll(Hall hall) {
        return hallMapper.selectAll(hall);
    }

    /**
     * 分页查询
     */
    public PageInfo<Hall> selectPage(Hall hall, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Hall> list = hallMapper.selectAll(hall);
        return PageInfo.of(list);
    }
}
