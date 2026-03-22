package com.qi.backend.mapper;

import java.util.List;

import com.qi.backend.entity.College;

public interface CollegeMapper {
    // 获取所有学院
    List<College> selectAllColleges();

    // 根据 ID 获取学院
    College selectCollegeById(Long collegleId);

    // 根据学院代码获取学院
    College selectCollegeByCode(String collegeCode);

    // 分页查询所有学院
    List<College> selectCollegesByPage();

    // 多条件分页查询
    List<College> selectCollegesByConditionPage(College college);

    // 新增学院
    int insertCollege(College college);

    // 修改学院
    int updateCollege(College college);
}
