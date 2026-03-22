package com.qi.backend.controller;

import com.github.pagehelper.PageInfo;
import com.qi.backend.entity.College;
import com.qi.backend.model.Result;
import com.qi.backend.service.CollegeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/college")
@RequiredArgsConstructor
public class CollegeController {

    private final CollegeService collegeService;

    /**
     * 获取所有学院
     * 前端传：{}
     */
    @PostMapping("/getAllColleges")
    public Result<List<College>> getAllColleges() {
        List<College> collegeList = collegeService.getAllColleges();
        return Result.success("查询所有学院成功", collegeList);
    }

    /**
     * 根据ID查询学院
     * 前端传：{ "collegeId": 1 }
     */
    @PostMapping("/getCollegeById")
    public Result<College> getCollegeById(@RequestBody Long collegeId) {
        if (collegeId == null) {
            return Result.error("学院ID不能为空");
        }
        College college = collegeService.getCollegeById(collegeId);
        return Result.success("根据ID查询学院成功", college);
    }

    /**
     * 根据学院编码查询学院
     * 前端传：{ "collegeCode": "25" }
     */
    @PostMapping("/getCollegeByCode")
    public Result<College> getCollegeByCode(@RequestBody String collegeCode) {
        if (collegeCode == null || collegeCode.trim().isEmpty()) {
            return Result.error("学院编码不能为空");
        }
        College college = collegeService.getCollegeByCode(collegeCode);
        return Result.success("根据编码查询学院成功", college);
    }

    /**
     * 分页查询学院
     * 前端传：{ "pageNum": 1, "pageSize": 10 }
     */
    @PostMapping("/getCollegesByPage")
    public Result<PageInfo<College>> getCollegesByPage(Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageSize == null) {
            return Result.error("页码和每页条数不能为空");
        }
        PageInfo<College> pageInfo = collegeService.getCollegesByPage(pageNum, pageSize);
        return Result.success("分页查询学院成功", pageInfo);
    }

    /**
     * 多条件分页查询学院
     * 前端直接传 College 对象 + pageNum、pageSize
     */
    @PostMapping("/getCollegesByConditionPage")
    public Result<PageInfo<College>> getCollegesByConditionPage(@RequestBody College college, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageSize == null) {
            return Result.error("页码和每页条数不能为空");
        }
        PageInfo<College> pageInfo = collegeService.getCollegesByConditionPage(college, pageNum, pageSize);
        return Result.success("多条件分页查询学院成功", pageInfo);
    }

    /**
     * 新增学院
     */
    @PostMapping("/addCollege")
    public Result<Boolean> addCollege(@RequestBody College college) {
        Boolean success = collegeService.addCollege(college);
        if (success) {
            return Result.success("新增学院成功", success);
        }
        return Result.error("新增学院失败");
    }

    /**
     * 修改学院信息
     */
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