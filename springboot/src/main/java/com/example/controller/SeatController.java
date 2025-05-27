package com.example.controller;

import com.example.common.Result;
import com.example.entity.Seat;
import com.example.service.SeatService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 座位前端操作接口
 */
@RestController
@RequestMapping("/seat")
public class SeatController {

    @Resource
    private SeatService seatService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Seat seat) {
        seatService.add(seat);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        seatService.deleteById(id);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result update(@RequestBody Seat seat) {
        seatService.update(seat);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Seat seat = seatService.selectById(id);
        return Result.success(seat);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Seat seat) {
        List<Seat> list = seatService.selectAll(seat);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Seat seat,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Seat> page = seatService.selectPage(seat, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 根据影厅ID查询座位
     */
    @GetMapping("/selectByHallId/{hallId}")
    public Result selectByHallId(@PathVariable Integer hallId) {
        List<Seat> list = seatService.selectByHallId(hallId);
        return Result.success(list);
    }
}
