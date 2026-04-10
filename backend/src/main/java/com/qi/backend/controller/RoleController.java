package com.qi.backend.controller;

import com.qi.backend.entity.Role;
import com.qi.backend.model.Result;
import com.qi.backend.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
@Tag(name = "角色管理")
public class RoleController {

    private final RoleService roleService;

    /**
     * 获取所有角色
     */
    @Operation(summary = "获取所有角色")
    @PostMapping("/getAllRoles")
    public Result<List<Role>> getAllRoles() {
        List<Role> roleList = roleService.getAllRoles();
        return Result.success("查询所有角色成功", roleList);
    }

    // ====================== 修复后的接口 ======================
    /**
     * 根据 ID 查询角色
     */
    @Operation(summary = "根据ID查询角色", description = "1")
    @PostMapping("/getRoleById")
    public Result<Role> getRoleById(@RequestBody Integer roleId) {
        if (roleId == null) {
            return Result.error("角色ID不能为空");
        }
        Role role = roleService.getRoleById(roleId);
        return Result.success("查询角色成功", role);
    }
    // =========================================================

    /**
     * 新增角色
     */
    @Operation(summary = "新增角色")
    @PostMapping("/addRole")
    public Result<Boolean> addRole(@RequestBody Role role) {
        Boolean success = roleService.addRole(role);
        if (success) {
            return Result.success("新增角色成功", success);
        }
        return Result.error("新增角色失败");
    }

    /**
     * 修改角色
     */
    @Operation(summary = "修改角色")
    @PostMapping("/updateRole")
    public Result<Boolean> updateRole(@RequestBody Role role) {
        if (role.getRoleId() == null) {
            return Result.error("角色ID不能为空");
        }
        Boolean success = roleService.updateRole(role);
        if (success) {
            return Result.success("修改角色成功", success);
        }
        return Result.error("修改角色失败");
    }
}