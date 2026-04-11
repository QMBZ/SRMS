package com.qi.backend.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ScoreImportExcel {

    @ExcelProperty(index = 0, value = "学号")
    private String studentNo;

    @ExcelProperty(index = 1, value = "课程名称")
    private String courseName;

    @ExcelProperty(index = 2, value = "成绩")
    private BigDecimal score;
}