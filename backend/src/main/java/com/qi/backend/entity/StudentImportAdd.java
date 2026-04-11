package com.qi.backend.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.Data;
import java.time.LocalDate;

@Data
public class StudentImportAdd {

    @ExcelProperty(value = "学号（必填）", index = 0)
    private String studentNo;

    @ExcelProperty(value = "姓名（必填）", index = 1)
    private String realName;

    @ExcelProperty(value = "性别（必填）", index = 2)
    private String gender;

    @ExcelProperty(value = "身份证号（必填）", index = 3)
    private String idCard;

    @ExcelProperty(value = "民族", index = 4)
    private String nation;

    @ExcelProperty(value = "籍贯", index = 5)
    private String nativePlace;

    @ExcelProperty(value = "手机号", index = 6)
    private String phone;

    @ExcelProperty(value = "邮箱", index = 7)
    private String email;

    @ExcelProperty(value = "学院ID（必填）", index = 8)
    private Long collegeId;

    @ExcelProperty(value = "专业ID（必填）", index = 9)
    private Long majorId;

    @ExcelProperty(value = "班级ID（必填）", index = 10)
    private Long classId;

    @ExcelProperty(value = "入学时间（必填）", index = 11)
    @DateTimeFormat("yyyy-MM-dd")
    private LocalDate enrollmentTime;
}