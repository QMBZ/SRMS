package com.qi.backend.service;

import java.util.List;

import com.qi.backend.model.ChangePasswordRequest;
import com.qi.backend.util.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

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
        List<User> list = userMapper.selectUsersByPage();
        // 包装成分页结果
        return new PageInfo<>(list);
    }

    /**
     * 多条件分页查询
     */
    public PageInfo<User> getUserByConditionPage(User user, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> list = userMapper.selectUsersByConditionPage(user);
        return new PageInfo<>(list);
    }

    /**
     * 新增用户
     */
    public Boolean addUser(User user) {
        int count = userMapper.insertUser(user);
        return count > 0;
    }

    /**
     * 修改用户信息
     */
    public Boolean updateUser(User user) {
        int count = userMapper.updateUser(user);
        return count > 0;
    }

    /**
     * 修改用户状态（启用/禁用）
     */
    public Boolean updateUserStatus(Long userId, Integer status) {
        int count = userMapper.updateUserStatus(userId, status);
        return count > 0;
    }

    /*
    * 修改用户密码
    *  */
    public Boolean updatePassword(ChangePasswordRequest changePasswordRequest) {
        /*
        * 获取当前userId的登录密码，并与旧密码比对是否相同
        * 将加密后的新密码进行替换
        * 调用Mapper进行修改
        *  */

        User user = getUserById(changePasswordRequest.getUserId());
        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
            return false;
        }

        String newPassword = passwordEncoder.encode(changePasswordRequest.getNewPassword());
        changePasswordRequest.setNewPassword(newPassword);

        int count = userMapper.updatePassword(changePasswordRequest);
        return count > 0;
    }

    /*
    * 重置用户密码
    *  */
    public Boolean resetPassword(Long userId) {
        String password = passwordEncoder.encode("123456");
        int count = userMapper.resetPassword(userId, password);
        return count > 0;
    }
}
