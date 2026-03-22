package com.qi.backend.controller;

import com.github.pagehelper.PageInfo;
import com.qi.backend.entity.StudentInfo;
import com.qi.backend.model.Result;
import com.qi.backend.service.StudentInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentInfoController {

    private final StudentInfoService studentInfoService;

    /**
     * 获取所有学生信息
     */
    @PostMapping("/getAllStudentInfos")
    public Result<List<StudentInfo>> getAllStudentInfos() {
        List<StudentInfo> studentList = studentInfoService.getAllStudentInfos();
        return Result.success("查询所有学生信息成功", studentList);
    }

    /**
     * 根据学生ID查询学生
     * 前端传：{ "studentId": 1 }
     */
    @PostMapping("/getStudentInfoById")
    public Result<StudentInfo> getStudentInfoById(@RequestBody Long studentId) {
        if (studentId == null) {
            return Result.error("学生ID不能为空");
        }
        StudentInfo studentInfo = studentInfoService.getStudentInfoById(studentId);
        return Result.success("根据ID查询学生成功", studentInfo);
    }

    /**
     * 根据学号查询学生
     * 前端传：{ "studentNo": "2026001" }
     */
    @PostMapping("/getStudentInfoByStudentNo")
    public Result<StudentInfo> getStudentInfoByStudentNo(@RequestBody String studentNo) {
        if (studentNo == null || studentNo.trim().isEmpty()) {
            return Result.error("学号不能为空");
        }
        StudentInfo studentInfo = studentInfoService.getStudentInfoByStudentNo(studentNo);
        return Result.success("根据学号查询学生成功", studentInfo);
    }

    /**
     * 根据身份证号查询学生
     * 前端传：{ "idCard": "110101..." }
     */
    @PostMapping("/getStudentInfoByIdCard")
    public Result<StudentInfo> getStudentInfoByIdCard(@RequestBody String idCard) {
        if (idCard == null || idCard.trim().isEmpty()) {
            return Result.error("身份证号不能为空");
        }
        StudentInfo studentInfo = studentInfoService.getStudentInfoByIdCard(idCard);
        return Result.success("根据身份证号查询学生成功", studentInfo);
    }

    /**
     * 根据学院ID查询学生列表
     * 前端传：{ "collegeId": 1 }
     */
    @PostMapping("/getStudentInfosByCollegeId")
    public Result<List<StudentInfo>> getStudentInfosByCollegeId(@RequestBody Long collegeId) {
        if (collegeId == null) {
            return Result.error("学院ID不能为空");
        }
        List<StudentInfo> studentList = studentInfoService.getStudentInfosByCollegeId(collegeId);
        return Result.success("根据学院ID查询学生成功", studentList);
    }

    /**
     * 根据专业ID查询学生列表
     * 前端传：{ "majorId": 1 }
     */
    @PostMapping("/getStudentInfosByMajorId")
    public Result<List<StudentInfo>> getStudentInfosByMajorId(@RequestBody Long majorId) {
        if (majorId == null) {
            return Result.error("专业ID不能为空");
        }
        List<StudentInfo> studentList = studentInfoService.getStudentInfosByMajorId(majorId);
        return Result.success("根据专业ID查询学生成功", studentList);
    }

    /**
     * 根据班级ID查询学生列表
     * 前端传：{ "classId": 1 }
     */
    @PostMapping("/getStudentInfosByClassId")
    public Result<List<StudentInfo>> getStudentInfosByClassId(@RequestBody Long classId) {
        if (classId == null) {
            return Result.error("班级ID不能为空");
        }
        List<StudentInfo> studentList = studentInfoService.getStudentInfosByClassId(classId);
        return Result.success("根据班级ID查询学生成功", studentList);
    }

    /**
     * 根据学籍状态查询学生列表
     * 前端传：{ "studentStatus": "在读" }
     */
    @PostMapping("/getStudentInfosByStatus")
    public Result<List<StudentInfo>> getStudentInfosByStatus(@RequestBody String studentStatus) {
        if (studentStatus == null || studentStatus.trim().isEmpty()) {
            return Result.error("学籍状态不能为空");
        }
        List<StudentInfo> studentList = studentInfoService.getStudentInfosByStatus(studentStatus);
        return Result.success("根据学籍状态查询学生成功", studentList);
    }

    /**
     * 分页查询学生
     * 前端传：{ "pageNum": 1, "pageSize": 10 }
     */
    @PostMapping("/getStudentInfoByPage")
    public Result<PageInfo<StudentInfo>> getStudentInfoByPage(Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageSize == null) {
            return Result.error("页码和每页条数不能为空");
        }
        PageInfo<StudentInfo> pageInfo = studentInfoService.getStudentInfoByPage(pageNum, pageSize);
        return Result.success("分页查询学生成功", pageInfo);
    }

    /**
     * 多条件分页查询学生
     * 前端直接传 StudentInfo 对象 + pageNum、pageSize
     */
    @PostMapping("/getStudentInfoByConditionPage")
    public Result<PageInfo<StudentInfo>> getStudentInfoByConditionPage(@RequestBody StudentInfo studentInfo, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageSize == null) {
            return Result.error("页码和每页条数不能为空");
        }
        PageInfo<StudentInfo> pageInfo = studentInfoService.getStudentInfoByConditionPage(studentInfo, pageNum, pageSize);
        return Result.success("多条件分页查询学生成功", pageInfo);
    }

    /**
     * 新增学生信息
     */
    @PostMapping("/addStudentInfo")
    public Result<Boolean> addStudentInfo(@RequestBody StudentInfo studentInfo) {
        Boolean success = studentInfoService.addStudentInfo(studentInfo);
        if (success) {
            return Result.success("新增学生信息成功", success);
        }
        return Result.error("新增学生信息失败");
    }

    /**
     * 修改学生信息
     */
    @PostMapping("/updateStudentInfo")
    public Result<Boolean> updateStudentInfo(@RequestBody StudentInfo studentInfo) {
        if (studentInfo.getStudentId() == null) {
            return Result.error("学生ID不能为空");
        }
        Boolean success = studentInfoService.updateStudentInfo(studentInfo);
        if (success) {
            return Result.success("修改学生信息成功", success);
        }
        return Result.error("修改学生信息失败");
    }
}