package com.qi.backend.service;

import com.qi.backend.entity.AdminCollege;
import com.qi.backend.mapper.AdminCollegeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminCollegeService {

    private final AdminCollegeMapper adminCollegeMapper;

    /**
     * 根据学院ID查询管理员
     */
    public List<AdminCollege> getByCollegeId(Long collegeId) {
        return adminCollegeMapper.selectByCollegeId(collegeId);
    }

    /**
     * 根据用户ID获取绑定的学院
     */
    public AdminCollege getByUserId(Long userId) {
        return adminCollegeMapper.selectByUserId(userId);
    }

    /**
     * 查询全部
     */
    public List<AdminCollege> getAll() {
        return adminCollegeMapper.selectAll();
    }

    /**
     * 新增绑定
     */
    public Boolean add(AdminCollege adminCollege) {
        int count = adminCollegeMapper.insertAdminCollege(adminCollege);
        return count > 0;
    }

    /**
     * 根据ID删除
     */
    public Boolean deleteById(Long id) {
        int count = adminCollegeMapper.deleteById(id);
        return count > 0;
    }

    /**
     * 根据用户ID+学院ID解绑
     */
    public Boolean unbind(Long userId, Long collegeId) {
        int count = adminCollegeMapper.deleteByUserIdAndCollegeId(userId, collegeId);
        return count > 0;
    }
}