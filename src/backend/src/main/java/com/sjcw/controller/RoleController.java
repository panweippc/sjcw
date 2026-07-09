package com.sjcw.controller;

import com.sjcw.common.Result;
import com.sjcw.entity.Role;
import com.sjcw.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/list")
    public Result<List<Role>> list(Role role) {
        List<Role> roles = roleService.selectRoleList(role);
        return Result.success(roles);
    }

    @GetMapping("/{id}")
    public Result<Role> getById(@PathVariable String id) {
        Role role = roleService.getById(id);
        return Result.success(role);
    }

    @PostMapping
    public Result<Void> save(@RequestBody Role role) {
        role.setCreatedTime(LocalDateTime.now());
        role.setUpdatedTime(LocalDateTime.now());
        roleService.save(role);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Role role) {
        role.setUpdatedTime(LocalDateTime.now());
        roleService.updateById(role);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable String id) {
        roleService.removeById(id);
        return Result.success();
    }

    @GetMapping("/{id}/permissions")
    public Result<List<String>> getRolePermissions(@PathVariable String id) {
        List<String> permissionIds = roleService.selectPermissionIdsByRoleId(id);
        return Result.success(permissionIds);
    }

    @PutMapping("/{id}/permissions")
    public Result<Void> saveRolePermissions(@PathVariable String id, @RequestBody Map<String, List<String>> params) {
        List<String> permissionIds = params.get("permissionIds");
        roleService.saveRolePermissions(id, permissionIds);
        return Result.success();
    }
}
