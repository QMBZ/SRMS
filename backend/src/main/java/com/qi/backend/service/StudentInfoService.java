package com.qi.backend.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import org.springframework.stereotype.Service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qi.backend.entity.StudentInfo;
import com.qi.backend.mapper.StudentInfoMapper;
import com.qi.backend.util.LocalDateConverter;

import jakarta.servlet.http.HttpServletResponse;
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

    /**
     * 导出空 Excel 模板
     */
    public void exportEmptyTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("学生信息导入模板", "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        // 重点修复：必须传空集合，不能传 null！
        EasyExcel.write(response.getOutputStream())
                .head(StudentInfo.class)
                .registerConverter(new LocalDateConverter())
                .excelType(ExcelTypeEnum.XLSX)
                .sheet("学生模板")
                .doWrite(List.of()); // 这里必须是空集合，修复下载失败
    }

    /**
     * 按条件导出学生（对接你现有的多条件模糊查询）
     */
    public void exportByCondition(StudentInfo condition, HttpServletResponse response) throws IOException {
        List<StudentInfo> list = studentInfoMapper.selectStudentInfosByConditionPage(condition);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("学生信息列表", "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream())
                .head(StudentInfo.class)
                .registerConverter(new LocalDateConverter())
                .sheet("学生数据")
                .doWrite(list);
    }

    /**
     * 导入 Excel → 批量更新（只修改有值的字段）
     * 规则：
     * 1. 必须传 studentId
     * 2. 只有 Excel 中有值的字段才更新
     * 3. 空字段不覆盖数据库
     */
    public void importExcel(List<StudentInfo> excelList) {
        for (StudentInfo excel : excelList) {
            // 必须有 ID 才能更新
            if (excel.getStudentId() == null) {
                continue;
            }
            // 从数据库查原始数据
            StudentInfo dbStudent = studentInfoMapper.selectStudentInfoById(excel.getStudentId());
            if (dbStudent == null) {
                continue;
            }

            // ========== 核心：只覆盖 Excel 里有值的字段 ==========
            if (excel.getStudentNo() != null && !excel.getStudentNo().isEmpty()) {
                dbStudent.setStudentNo(excel.getStudentNo());
            }
            if (excel.getRealName() != null && !excel.getRealName().isEmpty()) {
                dbStudent.setRealName(excel.getRealName());
            }
            if (excel.getGender() != null && !excel.getGender().isEmpty()) {
                dbStudent.setGender(excel.getGender());
            }
            if (excel.getIdCard() != null && !excel.getIdCard().isEmpty()) {
                dbStudent.setIdCard(excel.getIdCard());
            }
            if (excel.getNation() != null && !excel.getNation().isEmpty()) {
                dbStudent.setNation(excel.getNation());
            }
            if (excel.getNativePlace() != null && !excel.getNativePlace().isEmpty()) {
                dbStudent.setNativePlace(excel.getNativePlace());
            }
            if (excel.getPhone() != null && !excel.getPhone().isEmpty()) {
                dbStudent.setPhone(excel.getPhone());
            }
            if (excel.getEmail() != null && !excel.getEmail().isEmpty()) {
                dbStudent.setEmail(excel.getEmail());
            }
            if (excel.getPhotoUrl() != null && !excel.getPhotoUrl().isEmpty()) {
                dbStudent.setPhotoUrl(excel.getPhotoUrl());
            }
            if (excel.getCollegeId() != null) {
                dbStudent.setCollegeId(excel.getCollegeId());
            }
            if (excel.getMajorId() != null) {
                dbStudent.setMajorId(excel.getMajorId());
            }
            if (excel.getClassId() != null) {
                dbStudent.setClassId(excel.getClassId());
            }
            if (excel.getEnrollmentTime() != null) {
                dbStudent.setEnrollmentTime(excel.getEnrollmentTime());
            }
            if (excel.getGraduationTime() != null) {
                dbStudent.setGraduationTime(excel.getGraduationTime());
            }
            if (excel.getStudentStatus() != null && !excel.getStudentStatus().isEmpty()) {
                dbStudent.setStudentStatus(excel.getStudentStatus());
            }

            // 执行更新（你Mapper本来就是动态更新，完美匹配）
            studentInfoMapper.updateStudentInfoById(dbStudent);
        }
    }
}