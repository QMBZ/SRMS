package com.qi.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qi.backend.entity.User;
import com.qi.backend.mapper.UserMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    public List<User> getAllUsers() {
        return userMapper.selectAllUsers();
    }

    /**
     * 根据用户ID查询用户
     */
    public User getUserById(Long userId) {
        return userMapper.selectUserById(userId);
    }

    /**
     * 根据用户名（学号/工号）查询用户
     */
    public User getUserByUsername(String username) {
        return userMapper.selectUserByUsername(username);
    }

    /**
     * 根据角色ID查询用户列表
     */
    public List<User> getUsersByRoleId(Integer roleId) {
        return userMapper.selectUsersByRoleId(roleId);
    }

    /**
     * 根据账号状态查询用户列表
     */
    public List<User> getUsersByStatus(Integer status) {
        return userMapper.selectUsersByStatus(status);
    }

    /**
     * 分页查询用户
     * @param pageNum 页码
     * @param pageSize 每页条数
     */
    public PageInfo<User> getUserByPage(Integer pageNum, Integer pageSize) {
        // 开启分页
        PageHelper.startPage(pageNum, pageSize);
        List<User> list = userMapper.selectUserByPage();
        // 包装成分页结果
        return new PageInfo<>(list);
    }

    /**
     * 多条件分页查询
     */
    public PageInfo<User> getUserByConditionPage(User user, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> list = userMapper.selectUserByConditionPage(user);
        return new PageInfo<>(list);
    }

    /**
     * 新增用户
     */
    public void addUser(User user) {
        userMapper.insertUser(user);
    }

    /**
     * 修改用户信息
     */
    public void updateUser(User user) {
        userMapper.updateUser(user);
    }

    /**
     * 修改用户状态（启用/禁用）
     */
    public void updateUserStatus(Long userId, Integer status) {
        userMapper.updateUserStatus(userId, status);
    }
}
