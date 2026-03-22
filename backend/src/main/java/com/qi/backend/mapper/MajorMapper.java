package com.qi.backend.mapper;

import java.util.List;

import com.qi.backend.entity.Major;

public interface MajorMapper {
    // 获取所有专业
    List<Major> selectAllMajors();

    // 根据 ID 获取专业
    Major selectMajorById(Long majorId);

    // 根据专业代码获取专业
    Major selectMajorByCode(String majorCode);

    // 根据学院ID查询专业列表
    List<Major> selectMajorsByCollegeId(Long collegeId);

    // 分页查询所有专业
    List<Major> selectMajorsByPage();

    // 多条件分页查询
    List<Major> selectMajorsByConditionPage(Major major);

    // 新增专业
    int insertMajor(Major major);

    // 修改专业
    int updateMajor(Major major);
}
