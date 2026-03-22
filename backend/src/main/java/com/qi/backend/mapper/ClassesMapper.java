package com.qi.backend.mapper;

import java.util.List;

import com.qi.backend.entity.Classes;

public interface ClassesMapper {
    // 获取所有班级
    List<Classes> selectAllClasses();

    // 根据 ID 获取班级
    Classes selectClassById(Long classId);

    // 根据班级代码获取班级
    Classes selectClassByCode(String classesCode);

    // 根据专业ID查询班级
    List<Classes> selectClassesByMajorId(Long majorId);

    // 根据学院ID查询班级
    List<Classes> selectClassesByCollegeId(Long collegeId);

    // 分页查询班级
    List<Classes> selectClassesByPage();

    // 多条件分页查询
    List<Classes> selectClassesByConditionPage(Classes classes);

    // 新增班级
    int insertClass(Classes classes);

    // 修改班级
    int updateClass(Classes classes);
}
