package com.qi.backend.controller;

import com.alibaba.excel.EasyExcel;
import com.qi.backend.entity.Score;
import com.qi.backend.model.Result;
import com.qi.backend.model.ScoreImportExcel;
import com.qi.backend.model.StudentScoreGraduateModel;
import com.qi.backend.service.ScoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/score")
@RequiredArgsConstructor
@Tag(name = "成绩管理")
public class ScoreController {

    private final ScoreService scoreService;

    /**
     * 新增成绩
     */
    @Operation(summary = "新增成绩")
    @PostMapping("/addScore")
    public Result<Boolean> addScore(@RequestBody Score score) {
        Boolean success = scoreService.addScore(score);
        if (success) {
            return Result.success("新增成绩成功", success);
        }
        return Result.error("新增成绩失败");
    }

    /**
     * 修改成绩
     */
    @Operation(summary = "修改成绩")
    @PostMapping("/updateScore")
    public Result<Boolean> updateScore(@RequestBody Score score) {
        if (score.getId() == null) {
            return Result.error("成绩ID不能为空");
        }
        Boolean success = scoreService.updateScore(score);
        if (success) {
            return Result.success("修改成绩成功", success);
        }
        return Result.error("修改成绩失败");
    }

    /**
     * 根据ID查询成绩
     */
    @Operation(summary = "根据ID查询成绩")
    @PostMapping("/getScoreById")
    public Result<Score> getScoreById(@RequestBody Long id) {
        if (id == null) {
            return Result.error("成绩ID不能为空");
        }
        Score score = scoreService.getScoreById(id);
        return Result.success("查询成绩成功", score);
    }

    /**
     * 根据学号查询学生所有成绩
     */
    @Operation(summary = "根据学号查询学生所有成绩")
    @PostMapping("/getScoresByStudentNo")
    public Result<List<Score>> getScoresByStudentNo(@RequestBody String studentNo) {
        if (studentNo == null || studentNo.trim().isEmpty()) {
            return Result.error("学号不能为空");
        }
        List<Score> scoreList = scoreService.getScoresByStudentNo(studentNo);
        return Result.success("查询学生成绩成功", scoreList);
    }

    /**
     * 毕业资格统计列表（可按学院/专业筛选）
     */
    @Operation(summary = "毕业资格统计列表")
    @PostMapping("/getStudentScoreGraduateList")
    public Result<List<StudentScoreGraduateModel>> getStudentScoreGraduateList(
            @RequestBody(required = false) Map<String, Long> params) {

        Long collegeId = null;
        Long majorId = null;

        if (params != null) {
            collegeId = params.get("collegeId");
            majorId = params.get("majorId");
        }

        List<StudentScoreGraduateModel> list = scoreService.getStudentScoreGraduateList(collegeId, majorId);
        return Result.success("查询毕业资格统计成功", list);
    }

    /**
     * 导出成绩导入模板
     */
    @Operation(summary = "导出成绩导入模板")
    @PostMapping("/exportScoreTemplate")
    public void exportScoreTemplate(HttpServletResponse response) throws IOException {
        scoreService.exportScoreTemplate(response);
    }

    /**
     * 导入成绩（存在更新，不存在新增）
     */
    @Operation(summary = "导入成绩")
    @PostMapping("/importScore")
    public Result<Boolean> importScore(MultipartFile file) throws IOException {
        List<ScoreImportExcel> list = EasyExcel.read(file.getInputStream())
                .head(ScoreImportExcel.class)
                .sheet()
                .doReadSync();

        scoreService.importScoreExcel(list);
        return Result.success("成绩导入完成，已自动覆盖重复记录", Boolean.TRUE);
    }
}