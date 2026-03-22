package com.qi.backend.mapper;

import com.qi.backend.entity.AdminCollege;

import java.util.List;

public interface AdminCollegeMapper {
    // 根据学院ID查询所有管理员
    List<AdminCollege> selectByCollegeId(Long collegeId);

    // 查询全部记录
    List<AdminCollege> selectAll();

    // 新增学院管理员绑定
    int insertAdminCollege(AdminCollege adminCollege);

    // 根据ID删除
    int deleteById(Long id);

    // 根据 用户ID+学院ID 删除（解绑）
    int deleteByUserIdAndCollegeId(Long userId, Long collegeId);
}