package com.example.controller;

import com.example.common.Result;
import com.example.entity.Screening;
import com.example.service.ScreeningService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 放映场次前端操作接口
 */
@RestController
@RequestMapping("/screening")
public class ScreeningController {

    @Resource
    private ScreeningService screeningService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Screening screening) {
        screeningService.add(screening);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        screeningService.deleteById(id);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result update(@RequestBody Screening screening) {
        screeningService.update(screening);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Screening screening = screeningService.selectById(id);
        return Result.success(screening);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Screening screening) {
        List<Screening> list = screeningService.selectAll(screening);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Screening screening,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Screening> page = screeningService.selectPage(screening, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 根据电影ID查询场次
     */
    @GetMapping("/selectByFilmId/{filmId}")
    public Result selectByFilmId(@PathVariable Integer filmId) {
        List<Screening> list = screeningService.selectByFilmId(filmId);
        return Result.success(list);
    }
}
