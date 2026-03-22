package com.qi.backend.controller;

import com.qi.backend.entity.AdminCollege;
import com.qi.backend.model.Result;
import com.qi.backend.service.AdminCollegeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/adminCollege")
@RequiredArgsConstructor
public class AdminCollegeController {

    private final AdminCollegeService adminCollegeService;

    /**
     * 根据学院ID查询管理员
     */
    @PostMapping("/getByCollegeId")
    public Result<List<AdminCollege>> getByCollegeId(@RequestBody Long collegeId) {
        if (collegeId == null) {
            return Result.error("学院ID不能为空");
        }
        List<AdminCollege> list = adminCollegeService.getByCollegeId(collegeId);
        return Result.success("查询成功", list);
    }

    /**
     * 查询全部学院管理员
     */
    @PostMapping("/getAll")
    public Result<List<AdminCollege>> getAll() {
        List<AdminCollege> list = adminCollegeService.getAll();
        return Result.success("查询全部成功", list);
    }

    /**
     * 新增学院管理员绑定
     */
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