package com.qi.backend.controller;

import com.github.pagehelper.PageInfo;
import com.qi.backend.entity.College;
import com.qi.backend.model.Result;
import com.qi.backend.service.CollegeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/college")
@RequiredArgsConstructor
@Tag(name = "学院管理")
public class CollegeController {

    private final CollegeService collegeService;

    @Operation(summary = "获取所有学院", description = "查询系统中全部的学院信息列表")
    @PostMapping("/getAllColleges")
    public Result<List<College>> getAllColleges() {
        List<College> collegeList = collegeService.getAllColleges();
        return Result.success("查询所有学院成功", collegeList);
    }

    @Operation(summary = "根据ID查询学院", description = "通过学院ID查询单个学院详情信息")
    @PostMapping("/getCollegeById")
    public Result<College> getCollegeById(@RequestBody Long collegeId) {
        if (collegeId == null) {
            return Result.error("学院ID不能为空");
        }
        College college = collegeService.getCollegeById(collegeId);
        return Result.success("根据ID查询学院成功", college);
    }

    @Operation(summary = "根据学院编码查询学院", description = "通过学院唯一编码查询学院信息")
    @PostMapping("/getCollegeByCode")
    public Result<College> getCollegeByCode(@RequestBody String collegeCode) {
        if (collegeCode == null || collegeCode.trim().isEmpty()) {
            return Result.error("学院编码不能为空");
        }
        College college = collegeService.getCollegeByCode(collegeCode);
        return Result.success("根据编码查询学院成功", college);
    }

    @Operation(summary = "分页查询学院", description = "通过页码和每页条数分页获取学院列表")
    @PostMapping("/getCollegesByPage")
    public Result<PageInfo<College>> getCollegesByPage(Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageSize == null) {
            return Result.error("页码和每页条数不能为空");
        }
        PageInfo<College> pageInfo = collegeService.getCollegesByPage(pageNum, pageSize);
        return Result.success("分页查询学院成功", pageInfo);
    }

    @Operation(summary = "多条件分页查询学院", description = "根据学院信息条件+分页参数组合查询学院")
    @PostMapping("/getCollegesByConditionPage")
    public Result<PageInfo<College>> getCollegesByConditionPage(@RequestBody College college, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageSize == null) {
            return Result.error("页码和每页条数不能为空");
        }
        PageInfo<College> pageInfo = collegeService.getCollegesByConditionPage(college, pageNum, pageSize);
        return Result.success("多条件分页查询学院成功", pageInfo);
    }

    @Operation(summary = "新增学院")
    @PostMapping("/addCollege")
    public Result<Boolean> addCollege(@RequestBody College college) {
        Boolean success = collegeService.addCollege(college);
        if (success) {
            return Result.success("新增学院成功", success);
        }
        return Result.error("新增学院失败");
    }

    @Operation(summary = "修改学院信息")
    @PostMapping("/updateCollege")
    public Result<Boolean> updateCollege(@RequestBody College college) {
        if (college.getCollegeId() == null) {
            return Result.error("学院ID不能为空");
        }
        Boolean success = collegeService.updateCollege(college);
        if (success) {
            return Result.success("修改学院信息成功", success);
        }
        return Result.error("修改学院信息失败");
    }
}