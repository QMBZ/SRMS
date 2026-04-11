package com.qi.backend.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.util.StringUtils;
import com.qi.backend.entity.Score;
import com.qi.backend.mapper.ScoreMapper;
import com.qi.backend.model.ScoreImportExcel;
import com.qi.backend.model.StudentScoreGraduateModel;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScoreService {

    private final ScoreMapper scoreMapper;

    /**
     * 新增成绩
     */
    public Boolean addScore(Score score) {
        int count = scoreMapper.insertScore(score);
        return count > 0;
    }

    /**
     * 修改成绩
     */
    public Boolean updateScore(Score score) {
        int count = scoreMapper.updateScoreById(score);
        return count > 0;
    }

    /**
     * 根据ID查询成绩
     */
    public Score getScoreById(Long id) {
        return scoreMapper.selectScoreById(id);
    }

    /**
     * 根据学号查询该学生所有成绩
     */
    public List<Score> getScoresByStudentNo(String studentNo) {
        return scoreMapper.selectScoresByStudentNo(studentNo);
    }

    /**
     * 毕业资格统计列表（学院+专业筛选）
     */
    public List<StudentScoreGraduateModel> getStudentScoreGraduateList(Long collegeId, Long majorId) {
        return scoreMapper.selectStudentScoreGraduateList(collegeId, majorId);
    }

    public void exportScoreTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("成绩批量导入模板", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream())
                .head(ScoreImportExcel.class)
                .excelType(ExcelTypeEnum.XLSX)
                .sheet("成绩导入")
                .doWrite(List.of());
    }

    /**
     * 导入成绩（存在更新，不存在新增）
     */
    @Transactional(rollbackFor = Exception.class)
    public void importScoreExcel(List<ScoreImportExcel> excelList) {
        for (ScoreImportExcel excel : excelList) {
            try {
                // 1. 必填校验
                if (StringUtils.isBlank(excel.getStudentNo())
                        || StringUtils.isBlank(excel.getCourseName())
                        || excel.getScore() == null) {
                    continue;
                }

                // 2. 查询是否已存在（学号+课程名 唯一）
                Score exist = scoreMapper.selectByStudentNoAndCourseName(
                        excel.getStudentNo(),
                        excel.getCourseName()
                );

                if (exist != null) {
                    // 已存在 → 更新成绩
                    exist.setScore(excel.getScore());
                    scoreMapper.updateScoreById(exist);
                } else {
                    // 不存在 → 新增成绩
                    Score score = new Score();
                    score.setStudentNo(excel.getStudentNo());
                    score.setCourseName(excel.getCourseName());
                    score.setScore(excel.getScore());
                    scoreMapper.insertScore(score);
                }

            } catch (Exception e) {
                // 单条失败不影响整体
                e.printStackTrace();
            }
        }
    }
}