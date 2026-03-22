package com.qi.backend.controller;

import com.github.pagehelper.PageInfo;
import com.qi.backend.entity.User;
import com.qi.backend.model.Result;
import com.qi.backend.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "用户管理")
public class UserController {

    private final UserService userService;

    /**
     * 获取所有用户
     * 前端传：{}
     */
    @Operation(summary = "获取所有用户", description = "前端传：{}")
    @PostMapping("/getAllUsers")
    public Result<List<User>> getAllUsers() {
        List<User> userList = userService.getAllUsers();
        return Result.success("查询所有用户成功", userList);
    }

    /**
     * 根据用户ID查询用户
     * 前端传：{ "userId": 1 }
     */
    @Operation(summary = "根据用户ID查询用户", description = "前端传：{ \"userId\": 1 }")
    @PostMapping("/getUserById")
    public Result<User> getUserById(@RequestBody Long userId) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }
        User user = userService.getUserById(userId);
        return Result.success("根据ID查询用户成功", user);
    }

    /**
     * 根据用户名（学号/工号）查询用户
     * 前端传：{ "userName": "2026001" }
     */
    @Operation(summary = "根据用户名（学号/工号）查询用户", description = "前端传：{ \"userName\": \"2026001\" }")
    @PostMapping("/getUserByUsername")
    public Result<User> getUserByUsername(@RequestBody String userName) {
        if (userName == null || userName.trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        User user = userService.getUserByUsername(userName);
        return Result.success("根据用户名查询用户成功", user);
    }

    /**
     * 根据角色ID查询用户列表
     * 前端传：{ "roleId": 1 }
     */
    @Operation(summary = "根据角色ID查询用户列表", description = "前端传：{ \"roleId\": 1 }")
    @PostMapping("/getUsersByRoleId")
    public Result<List<User>> getUsersByRoleId(@RequestBody Integer roleId) {
        if (roleId == null) {
            return Result.error("角色ID不能为空");
        }
        List<User> userList = userService.getUsersByRoleId(roleId);
        return Result.success("根据角色ID查询用户成功", userList);
    }

    /**
     * 根据账号状态查询用户列表
     * 前端传：{ "status": 1 }
     */
    @Operation(summary = "根据账号状态查询用户列表", description = "前端传：{ \"status\": 1 }")
    @PostMapping("/getUsersByStatus")
    public Result<List<User>> getUsersByStatus(@RequestBody Integer status) {
        if (status == null) {
            return Result.error("账号状态不能为空");
        }
        List<User> userList = userService.getUsersByStatus(status);
        return Result.success("根据账号状态查询用户成功", userList);
    }

    /**
     * 分页查询用户
     * 前端传：{ "pageNum": 1, "pageSize": 10 }
     */
    @Operation(summary = "分页查询用户", description = "前端传：{ \"pageNum\": 1, \"pageSize\": 10 }")
    @PostMapping("/getUserByPage")
    public Result<PageInfo<User>> getUserByPage(Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageSize == null) {
            return Result.error("页码和每页条数不能为空");
        }
        PageInfo<User> pageInfo = userService.getUserByPage(pageNum, pageSize);
        return Result.success("分页查询用户成功", pageInfo);
    }

    /**
     * 多条件分页查询用户
     * 前端直接传 User 对象 + pageNum、pageSize
     */
    @Operation(summary = "多条件分页查询用户", description = "前端直接传 User 对象 + pageNum、pageSize")
    @PostMapping("/getUserByConditionPage")
    public Result<PageInfo<User>> getUserByConditionPage(@RequestBody User user, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageSize == null) {
            return Result.error("页码和每页条数不能为空");
        }
        PageInfo<User> pageInfo = userService.getUserByConditionPage(user, pageNum, pageSize);
        return Result.success("多条件分页查询用户成功", pageInfo);
    }

    /**
     * 新增用户
     */
    @Operation(summary = "新增用户")
    @PostMapping("/addUser")
    public Result<Boolean> addUser(@RequestBody User user) {
        Boolean success = userService.addUser(user);
        if (success) {
            return Result.success("新增用户成功", success);
        }
        return Result.error("新增用户失败");
    }

    /**
     * 修改用户信息
     */
    @Operation(summary = "修改用户信息")
    @PostMapping("/updateUser")
    public Result<Boolean> updateUser(@RequestBody User user) {
        if (user.getUserId() == null) {
            return Result.error("用户ID不能为空");
        }
        Boolean success = userService.updateUser(user);
        if (success) {
            return Result.success("修改用户信息成功", success);
        }
        return Result.error("修改用户信息失败");
    }

    /**
     * 修改用户状态（启用/禁用）
     * 前端传：{ "userId": 1, "status": 0 }
     */
    @Operation(summary = "修改用户状态（启用/禁用）", description = "前端传：{ \"userId\": 1, \"status\": 0 }")
    @PostMapping("/updateUserStatus")
    public Result<Boolean> updateUserStatus(@RequestBody User user) {
        Long userId = user.getUserId();
        Integer status = user.getStatus();
        if (userId == null || status == null) {
            return Result.error("用户ID和状态不能为空");
        }
        Boolean success = userService.updateUserStatus(userId, status);
        if (success) {
            return Result.success("修改用户状态成功", success);
        }
        return Result.error("修改用户状态失败");
    }
}