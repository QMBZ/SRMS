package com.qi.backend.model;

import lombok.Data;

@Data
public class StudentScoreGraduateModel {
    private Long studentId;         // 学生 ID
    private String studentNo;       // 学号
    private String realName;        // 姓名
    private String collegeName;     // 学院名称
    private String majorName;       // 专业名称
    private Integer failCount;      // 不及格科目数量
    private Boolean graduateStatus; // 毕业资格：true=准予毕业，false=不予毕业
}
