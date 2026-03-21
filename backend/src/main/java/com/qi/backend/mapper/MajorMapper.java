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
}
