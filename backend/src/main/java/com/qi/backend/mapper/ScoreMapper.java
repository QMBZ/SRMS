package com.qi.backend.mapper;

import java.util.List;

import com.qi.backend.entity.Score;
import com.qi.backend.model.StudentScoreGraduateModel;

public interface ScoreMapper {

    // 新增成绩
    int insertScore(Score score);

    // 修改成绩
    int updateScoreById(Score score);

    // 根据学号查询该学生所有成绩
    List<Score> selectScoresByStudentNo(String studentNo);

    // 根据ID查询成绩（用于修改）
    Score selectScoreById(Long id);

    // ==================== 前端毕业资格展示专用 ====================
    // 根据学院ID、专业ID查询学生成绩统计（不及格数+毕业资格）
    List<StudentScoreGraduateModel> selectStudentScoreGraduateList(Long collegeId, Long majorId);

    // 根据 学号 + 课程名 查询成绩（用于导入判重）
    Score selectByStudentNoAndCourseName(String studentNo, String courseName);
}