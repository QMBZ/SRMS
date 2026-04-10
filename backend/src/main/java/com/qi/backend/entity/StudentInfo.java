package com.qi.backend.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class StudentInfo {

    @ExcelProperty(value = "学生ID", index = 0)
    private Long studentId;             // 学生 ID，自增

    @ExcelProperty(value = "学号", index = 1)
    private String studentNo;           // 学号

    @ExcelProperty(value = "姓名", index = 2)
    private String realName;            // 学生真实姓名

    @ExcelProperty(value = "性别", index = 3)
    private String gender;              // 学生性别

    @ExcelProperty(value = "身份证号", index = 4)
    private String idCard;              // 学生身份证号

    @ExcelProperty(value = "民族", index = 5)
    private String nation;              // 学生民族

    @ExcelProperty(value = "籍贯", index = 6)
    private String nativePlace;         // 学生籍贯

    @ExcelProperty(value = "电话", index = 7)
    private String phone;               // 学生电话

    @ExcelProperty(value = "邮箱", index = 8)
    private String email;               // 学生邮箱

    @ExcelProperty(value = "头像URL", index = 9)
    private String photoUrl;            // 学生头像 URL

    @ExcelProperty(value = "学院ID", index = 10)
    private Long collegeId;             // 学院 ID

    @ExcelProperty(value = "专业ID", index = 11)
    private Long majorId;               // 专业 ID

    @ExcelProperty(value = "班级ID", index = 12)
    private Long classId;               // 班级 ID

    // 自定义日期格式化
    @ExcelProperty(value = "入学时间", index = 13)
    @DateTimeFormat("yyyy-MM-dd")
    private LocalDate enrollmentTime;   // 入学时间

    @ExcelProperty(value = "毕业时间", index = 14)
    @DateTimeFormat("yyyy-MM-dd")
    private LocalDate graduationTime;   // 毕业时间

    @ExcelProperty(value = "学籍状态", index = 15)
    private String studentStatus;       // 学籍状态

    // 导出不需要这两个
    @ExcelIgnore
    private LocalDateTime createTime;

    @ExcelIgnore
    private LocalDateTime updateTime;
}
