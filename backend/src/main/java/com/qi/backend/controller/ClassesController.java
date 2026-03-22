package com.qi.backend.controller;

import com.github.pagehelper.PageInfo;
import com.qi.backend.entity.Classes;
import com.qi.backend.model.Result;
import com.qi.backend.service.ClassesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/classes")
@RequiredArgsConstructor
@Tag(name = "班级管理")
public class ClassesController {

    private final ClassesService classesService;

    @Operation(summary = "获取所有班级")
    @PostMapping("/getAllClasses")
    public Result<List<Classes>> getAllClasses() {
        List<Classes> classesList = classesService.getAllClasses();
        return Result.success("查询所有班级成功", classesList);
    }

    @Operation(summary = "根据ID查询班级")
    @PostMapping("/getClassById")
    public Result<Classes> getClassById(@RequestBody Long classId) {
        if (classId == null) {
            return Result.error("班级ID不能为空");
        }
        Classes classes = classesService.getClassById(classId);
        return Result.success("根据ID查询班级成功", classes);
    }

    @Operation(summary = "根据班级编码查询班级")
    @PostMapping("/getClassByCode")
    public Result<Classes> getClassByCode(@RequestBody String classCode) {
        if (classCode == null || classCode.trim().isEmpty()) {
            return Result.error("班级编码不能为空");
        }
        Classes classes = classesService.getClassByCode(classCode);
        return Result.success("根据编码查询班级成功", classes);
    }

    @Operation(summary = "根据专业ID查询班级列表")
    @PostMapping("/getClassesByMajorId")
    public Result<List<Classes>> getClassesByMajorId(@RequestBody Long majorId) {
        if (majorId == null) {
            return Result.error("专业ID不能为空");
        }
        List<Classes> classesList = classesService.getClassesByMajorId(majorId);
        return Result.success("根据专业ID查询班级成功", classesList);
    }

    @Operation(summary = "根据学院ID查询班级列表")
    @PostMapping("/getClassesByCollegeId")
    public Result<List<Classes>> getClassesByCollegeId(@RequestBody Long collegeId) {
        if (collegeId == null) {
            return Result.error("学院ID不能为空");
        }
        List<Classes> classesList = classesService.getClassesByCollegeId(collegeId);
        return Result.success("根据学院ID查询班级成功", classesList);
    }

    @Operation(summary = "分页查询班级")
    @PostMapping("/getClassesByPage")
    public Result<PageInfo<Classes>> getClassesByPage(Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageSize == null) {
            return Result.error("页码和每页条数不能为空");
        }
        PageInfo<Classes> pageInfo = classesService.getClassesByPage(pageNum, pageSize);
        return Result.success("分页查询班级成功", pageInfo);
    }

    @Operation(summary = "多条件分页查询班级")
    @PostMapping("/getClassesByConditionPage")
    public Result<PageInfo<Classes>> getClassesByConditionPage(@RequestBody Classes classes, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageSize == null) {
            return Result.error("页码和每页条数不能为空");
        }
        PageInfo<Classes> pageInfo = classesService.getClassesByConditionPage(classes, pageNum, pageSize);
        return Result.success("多条件分页查询班级成功", pageInfo);
    }

    @Operation(summary = "新增班级")
    @PostMapping("/addClass")
    public Result<Boolean> addClass(@RequestBody Classes classes) {
        Boolean success = classesService.addClass(classes);
        if (success) {
            return Result.success("新增班级成功", success);
        }
        return Result.error("新增班级失败");
    }

    @Operation(summary = "修改班级信息")
    @PostMapping("/updateClass")
    public Result<Boolean> updateClass(@RequestBody Classes classes) {
        if (classes.getClassId() == null) {
            return Result.error("班级ID不能为空");
        }
        Boolean success = classesService.updateClass(classes);
        if (success) {
            return Result.success("修改班级信息成功", success);
        }
        return Result.error("修改班级信息失败");
    }
}