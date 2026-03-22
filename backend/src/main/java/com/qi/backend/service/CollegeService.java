package com.qi.backend.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qi.backend.entity.College;
import com.qi.backend.mapper.CollegeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CollegeService {

    private final CollegeMapper collegeMapper;

    /**
     * 获取所有学院
     */
    public List<College> getAllColleges() {
        return collegeMapper.selectAllColleges();
    }

    /**
     * 根据ID查询学院
     */
    public College getCollegeById(Long collegeId) {
        return collegeMapper.selectCollegeById(collegeId);
    }

    /**
     * 根据学院编码查询学院
     */
    public College getCollegeByCode(String collegeCode) {
        return collegeMapper.selectCollegeByCode(collegeCode);
    }

    /**
     * 分页查询学院
     */
    public PageInfo<College> getCollegesByPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<College> list = collegeMapper.selectCollegesByPage();
        return new PageInfo<>(list);
    }

    /**
     * 多条件分页查询
     */
    public PageInfo<College> getCollegesByConditionPage(College college, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<College> list = collegeMapper.selectCollegesByConditionPage(college);
        return new PageInfo<>(list);
    }

    /**
     * 新增学院
     */
    public void addCollege(College college) {
        collegeMapper.insertCollege(college);
    }

    /**
     * 修改学院
     */
    public void updateCollege(College college) {
        collegeMapper.updateCollege(college);
    }
}