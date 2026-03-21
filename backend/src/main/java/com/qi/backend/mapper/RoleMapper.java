package com.qi.backend.mapper;

import java.util.List;

import com.qi.backend.entity.Role;

public interface RoleMapper {
    // 获取所有角色类型
    List<Role> selectAllRoles();

    // 根据 ID 获取角色
    Role selectRoleById(Integer roleId);
}
