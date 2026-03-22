package com.qi.backend.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qi.backend.entity.Classes;
import com.qi.backend.mapper.ClassesMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassesService {

    private final ClassesMapper classesMapper;

    /**
     * 获取所有班级
     */
    public List<Classes> getAllClasses() {
        return classesMapper.selectAllClasses();
    }

    /**
     * 根据ID查询班级
     */
    public Classes getClassById(Long classId) {
        return classesMapper.selectClassById(classId);
    }

    /**
     * 根据班级编码查询班级
     */
    public Classes getClassByCode(String classCode) {
        return classesMapper.selectClassByCode(classCode);
    }

    /**
     * 根据专业ID查询班级列表
     */
    public List<Classes> getClassesByMajorId(Long majorId) {
        return classesMapper.selectClassesByMajorId(majorId);
    }

    /**
     * 根据学院ID查询班级列表
     */
    public List<Classes> getClassesByCollegeId(Long collegeId) {
        return classesMapper.selectClassesByCollegeId(collegeId);
    }

    /**
     * 分页查询班级
     */
    public PageInfo<Classes> getClassesByPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Classes> list = classesMapper.selectClassesByPage();
        return new PageInfo<>(list);
    }

    /**
     * 多条件分页查询
     */
    public PageInfo<Classes> getClassesByConditionPage(Classes classes, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Classes> list = classesMapper.selectClassesByConditionPage(classes);
        return new PageInfo<>(list);
    }

    /**
     * 新增班级
     */
    public void addClass(Classes classes) {
        classesMapper.insertClass(classes);
    }

    /**
     * 修改班级
     */
    public void updateClass(Classes classes) {
        classesMapper.updateClass(classes);
    }
}