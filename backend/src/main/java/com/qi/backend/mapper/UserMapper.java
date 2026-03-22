package com.qi.backend.mapper;

import java.util.List;

import com.qi.backend.entity.User;

public interface UserMapper {
    // 获取所有用户
    List<User> selectAllUsers();

    // 根据 ID 获取用户
    User selectUserById(Long userId);

    // 根据用户名（学号/工号）获取用户
    User selectUserByUsername(String username);

    // 通过角色筛选用户
    List<User> selectUsersByRoleId(Integer roleId);

    // 通过账号状态筛选
    List<User> selectUsersByStatus(Integer status);

    // 分页查询所有用户（配合 PageHelper 使用）
    List<User> selectUsersByPage();

    // 多条件分页查询（支持：姓名、用户名、角色、状态）
    List<User> selectUsersByConditionPage(User user);

    // 新增用户
    int insertUser(User user);

    // 修改用户
    int updateUser(User user);

    // 修改用户状态（禁用/启用）
    int updateUserStatus(Long userId, Integer status);
}
