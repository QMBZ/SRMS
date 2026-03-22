package com.qi.backend.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qi.backend.entity.Major;
import com.qi.backend.mapper.MajorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MajorService {

    private final MajorMapper majorMapper;

    /**
     * 获取所有专业
     */
    public List<Major> getAllMajors() {
        return majorMapper.selectAllMajors();
    }

    /**
     * 根据ID查询专业
     */
    public Major getMajorById(Long majorId) {
        return majorMapper.selectMajorById(majorId);
    }

    /**
     * 根据专业编码查询专业
     */
    public Major getMajorByCode(String majorCode) {
        return majorMapper.selectMajorByCode(majorCode);
    }

    /**
     * 根据学院ID查询专业列表
     */
    public List<Major> getMajorsByCollegeId(Long collegeId) {
        return majorMapper.selectMajorsByCollegeId(collegeId);
    }

    /**
     * 分页查询专业
     */
    public PageInfo<Major> getMajorsByPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Major> list = majorMapper.selectMajorsByPage();
        return new PageInfo<>(list);
    }

    /**
     * 多条件分页查询
     */
    public PageInfo<Major> getMajorsByConditionPage(Major major, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Major> list = majorMapper.selectMajorsByConditionPage(major);
        return new PageInfo<>(list);
    }

    /**
     * 新增专业
     */
    public void addMajor(Major major) {
        majorMapper.insertMajor(major);
    }

    /**
     * 修改专业
     */
    public void updateMajor(Major major) {
        majorMapper.updateMajor(major);
    }
}