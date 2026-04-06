package com.qi.backend.controller;

import com.qi.backend.entity.AdminCollege;
import com.qi.backend.model.Result;
import com.qi.backend.service.AdminCollegeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/adminCollege")
@RequiredArgsConstructor
@Tag(name = "学院管理员管理", description = "学院管理员的绑定/解绑/查询/删除接口")
public class AdminCollegeController {

    private final AdminCollegeService adminCollegeService;

    /**
     * 根据学院ID查询管理员
     */
    @Operation(summary = "根据学院ID查询管理员", description = "传入学院ID，返回该学院下的所有管理员记录")
    @PostMapping("/getByCollegeId")
    public Result<List<AdminCollege>> getByCollegeId(@RequestBody Long collegeId) {
        if (collegeId == null) {
            return Result.error("学院ID不能为空");
        }
        List<AdminCollege> list = adminCollegeService.getByCollegeId(collegeId);
        return Result.success("查询成功", list);
    }

    @PostMapping("/getByUserId")
    public Result<AdminCollege> getByUserId(@RequestBody Long userId) {
        if (userId == null) {
            return Result.error("学院ID不能为空");
        }
        AdminCollege adminCollege = adminCollegeService.getByUserId(userId);
        return Result.success("查询成功", adminCollege);
    }

    /**
     * 查询全部学院管理员
     */
    @Operation(summary = "查询所有学院管理员", description = "返回系统中所有学院管理员的记录")
    @PostMapping("/getAll")
    public Result<List<AdminCollege>> getAll() {
        List<AdminCollege> list = adminCollegeService.getAll();
        return Result.success("查询全部成功", list);
    }

    /**
     * 新增学院管理员绑定
     */
    @Operation(summary = "新增学院管理员", description = "将一个管理员绑定到某个学院，需要传入userId和collegeId")
    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody AdminCollege adminCollege) {
        if (adminCollege.getUserId() == null || adminCollege.getCollegeId() == null) {
            return Result.error("管理员ID和学院ID不能为空");
        }
        Boolean success = adminCollegeService.add(adminCollege);
        return success ? Result.success("绑定成功", true) : Result.error("绑定失败");
    }

    /**
     * 根据ID删除绑定
     */
    @Operation(summary = "根据绑定ID删除", description = "根据绑定表主键ID删除单条管理员-学院绑定关系")
    @PostMapping("/deleteById")
    public Result<Boolean> deleteById(@RequestBody Long id) {
        if (id == null) {
            return Result.error("ID不能为空");
        }
        Boolean success = adminCollegeService.deleteById(id);
        return success ? Result.success("删除成功", true) : Result.error("删除失败");
    }

    /**
     * 解绑：根据管理员ID+学院ID
     */
    @Operation(summary = "根据管理员ID+学院ID解绑", description = "根据userId和collegeId删除绑定关系")
    @PostMapping("/unbind")
    public Result<Boolean> unbind(@RequestBody AdminCollege adminCollege) {
        Long userId = adminCollege.getUserId();
        Long collegeId = adminCollege.getCollegeId();
        if (userId == null || collegeId == null) {
            return Result.error("管理员ID和学院ID不能为空");
        }
        Boolean success = adminCollegeService.unbind(userId, collegeId);
        return success ? Result.success("解绑成功", true) : Result.error("解绑失败");
    }
}