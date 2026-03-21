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
}
