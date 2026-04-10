package com.qi.backend.controller;

import com.github.pagehelper.PageInfo;
import com.qi.backend.entity.Major;
import com.qi.backend.model.Result;
import com.qi.backend.service.MajorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/major")
@RequiredArgsConstructor
@Tag(name = "专业管理")
public class MajorController {

    private final MajorService majorService;

    /**
     * 获取所有专业
     */
    @Operation(summary = "获取所有专业", description = "前端传：{}")
    @PostMapping("/getAllMajors")
    public Result<List<Major>> getAllMajors() {
        List<Major> majorList = majorService.getAllMajors();
        return Result.success("查询所有专业成功", majorList);
    }

    /**
     * 根据ID查询专业
     */
    @Operation(summary = "根据ID查询专业", description = "前端传：1")
    @PostMapping("/getMajorById")
    public Result<Major> getMajorById(@RequestBody Long majorId) {
        if (majorId == null) {
            return Result.error("专业ID不能为空");
        }
        Major major = majorService.getMajorById(majorId);
        return Result.success("根据ID查询专业成功", major);
    }

    /**
     * 根据专业编码查询专业
     */
    @Operation(summary = "根据专业编码查询专业", description = "前端传：2531")
    @PostMapping("/getMajorByCode")
    public Result<Major> getMajorByCode(@RequestBody String majorCode) {
        if (majorCode == null || majorCode.trim().isEmpty()) {
            return Result.error("专业编码不能为空");
        }
        Major major = majorService.getMajorByCode(majorCode);
        return Result.success("根据编码查询专业成功", major);
    }

    /**
     * 根据学院ID查询专业列表
     */
    @Operation(summary = "根据学院ID查询专业列表", description = "前端传：1")
    @PostMapping("/getMajorsByCollegeId")
    public Result<List<Major>> getMajorsByCollegeId(@RequestBody Long collegeId) {
        if (collegeId == null) {
            return Result.error("学院ID不能为空");
        }
        List<Major> majorList = majorService.getMajorsByCollegeId(collegeId);
        return Result.success("根据学院ID查询专业成功", majorList);
    }

    /**
     * 分页查询专业
     */
    @Operation(summary = "分页查询专业")
    @PostMapping("/getMajorsByPage")
    public Result<PageInfo<Major>> getMajorsByPage(Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageSize == null) {
            return Result.error("页码和每页条数不能为空");
        }
        PageInfo<Major> pageInfo = majorService.getMajorsByPage(pageNum, pageSize);
        return Result.success("分页查询专业成功", pageInfo);
    }

    /**
     * 多条件分页查询专业
     * 前端直接传 Major 对象 + pageNum、pageSize
     */
    @Operation(summary = "多条件分页查询专业", description = "前端直接传 Major 对象 + pageNum、pageSize")
    @PostMapping("/getMajorsByConditionPage")
    public Result<PageInfo<Major>> getMajorsByConditionPage(@RequestBody Major major, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageSize == null) {
            return Result.error("页码和每页条数不能为空");
        }
        PageInfo<Major> pageInfo = majorService.getMajorsByConditionPage(major, pageNum, pageSize);
        return Result.success("多条件分页查询专业成功", pageInfo);
    }

    /**
     * 新增专业
     */
    @Operation(summary = "新增专业")
    @PostMapping("/addMajor")
    public Result<Boolean> addMajor(@RequestBody Major major) {
        Boolean success = majorService.addMajor(major);
        if (success) {
            return Result.success("新增专业成功", success);
        }
        return Result.error("新增专业失败");
    }

    /**
     * 修改专业信息
     */
    @Operation(summary = "修改专业信息")
    @PostMapping("/updateMajor")
    public Result<Boolean> updateMajor(@RequestBody Major major) {
        if (major.getMajorId() == null) {
            return Result.error("专业ID不能为空");
        }
        Boolean success = majorService.updateMajor(major);
        if (success) {
            return Result.success("修改专业信息成功", success);
        }
        return Result.error("修改专业信息失败");
    }
}