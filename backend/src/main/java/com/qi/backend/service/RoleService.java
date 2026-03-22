package com.qi.backend.service;

import com.qi.backend.entity.Role;
import com.qi.backend.mapper.RoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleMapper roleMapper;

    /**
     * 获取所有角色
     */
    public List<Role> getAllRoles() {
        return roleMapper.selectAllRoles();
    }

    /**
     * 根据ID查询角色
     */
    public Role getRoleById(Integer roleId) {
        return roleMapper.selectRoleById(roleId);
    }

    /**
     * 新增角色
     */
    public Boolean addRole(Role role) {
        int count = roleMapper.insertRole(role);
        return count > 0;
    }

    /**
     * 修改角色
     */
    public Boolean updateRole(Role role) {
        int count = roleMapper.updateRole(role);
        return count > 0;
    }
}