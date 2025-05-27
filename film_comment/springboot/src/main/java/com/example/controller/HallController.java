package com.example.controller;

import com.example.common.Result;
import com.example.entity.Hall;
import com.example.service.HallService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 影厅前端操作接口
 */
@RestController
@RequestMapping("/hall")
public class HallController {

    @Resource
    private HallService hallService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Hall hall) {
        hallService.add(hall);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        hallService.deleteById(id);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result update(@RequestBody Hall hall) {
        hallService.update(hall);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Hall hall = hallService.selectById(id);
        return Result.success(hall);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Hall hall) {
        List<Hall> list = hallService.selectAll(hall);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Hall hall,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Hall> page = hallService.selectPage(hall, pageNum, pageSize);
        return Result.success(page);
    }
}
