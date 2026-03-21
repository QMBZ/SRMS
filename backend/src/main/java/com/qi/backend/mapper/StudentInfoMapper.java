package com.qi.backend.mapper;

import java.util.List;

import com.qi.backend.entity.StudentInfo;

public interface StudentInfoMapper {
    // 获取所有学生信息
    List<StudentInfo> selectAllStudentInfos();

    // 根据 ID 获取学生信息
    StudentInfo selectStudentInfoById(Long studentId);

    // 根据学号获取学生信息
    StudentInfo selectStudentInfoByStudentNo(String studentNo);

    // 根据身份证号获取学生信息
    StudentInfo selectStudentInfoByIdCard(String idCard);

    // 根据学院ID查询学生列表
    List<StudentInfo> selectStudentInfosByCollegeId(Long collegeId);

     // 根据专业ID查询学生列表
    List<StudentInfo> selectStudentInfosByMajorId(Long majorId);

     // 根据班级ID查询学生列表
    List<StudentInfo> selectStudentInfosByClassId(Long classId);

    // 根据学籍状态查询下学生列表
    List<StudentInfo> selectStudentInfosByStatus(String studentStatus);
}
