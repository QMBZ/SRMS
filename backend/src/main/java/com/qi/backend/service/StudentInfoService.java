package com.qi.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qi.backend.entity.StudentInfo;
import com.qi.backend.mapper.StudentInfoMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentInfoService {

    private final StudentInfoMapper studentInfoMapper;

    /**
     * 获取所有学生信息
     */
    public List<StudentInfo> getAllStudentInfos() {
        return studentInfoMapper.selectAllStudentInfos();
    }

    /**
     * 根据学生ID查询学生
     */
    public StudentInfo getStudentInfoById(Long studentId) {
        return studentInfoMapper.selectStudentInfoById(studentId);
    }

    /**
     * 根据学号查询学生
     */
    public StudentInfo getStudentInfoByStudentNo(String studentNo) {
        return studentInfoMapper.selectStudentInfoByStudentNo(studentNo);
    }

    /**
     * 根据身份证号查询学生
     */
    public StudentInfo getStudentInfoByIdCard(String idCard) {
        return studentInfoMapper.selectStudentInfoByIdCard(idCard);
    }

    /**
     * 根据学院ID查询学生列表
     */
    public List<StudentInfo> getStudentInfosByCollegeId(Long collegeId) {
        return studentInfoMapper.selectStudentInfosByCollegeId(collegeId);
    }

    /**
     * 根据专业ID查询学生列表
     */
    public List<StudentInfo> getStudentInfosByMajorId(Long majorId) {
        return studentInfoMapper.selectStudentInfosByMajorId(majorId);
    }

    /**
     * 根据班级ID查询学生列表
     */
    public List<StudentInfo> getStudentInfosByClassId(Long classId) {
        return studentInfoMapper.selectStudentInfosByClassId(classId);
    }

    /**
     * 根据学籍状态查询学生列表
     */
    public List<StudentInfo> getStudentInfosByStatus(String studentStatus) {
        return studentInfoMapper.selectStudentInfosByStatus(studentStatus);
    }

    /**
     * 分页查询学生
     * @param pageNum 页码
     * @param pageSize 每页条数
     */
    public PageInfo<StudentInfo> getStudentInfoByPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<StudentInfo> list = studentInfoMapper.selectStudentInfosByPage();
        return new PageInfo<>(list);
    }

    /**
     * 多条件分页查询
     */
    public PageInfo<StudentInfo> getStudentInfoByConditionPage(StudentInfo studentInfo, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<StudentInfo> list = studentInfoMapper.selectStudentInfosByConditionPage(studentInfo);
        return new PageInfo<>(list);
    }

    /**
     * 新增学生信息
     */
    public Boolean addStudentInfo(StudentInfo studentInfo) {
        int count = studentInfoMapper.insertStudentInfo(studentInfo);
        return count > 0;
    }

    /**
     * 修改学生信息
     */
    public Boolean updateStudentInfo(StudentInfo studentInfo) {
        int count = studentInfoMapper.updateStudentInfoById(studentInfo);
        return count > 0;
    }
}