package com.sjcw.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sjcw.entity.Role;

import java.util.List;

public interface RoleService extends IService<Role> {

    List<Role> selectRoleList(Role role);

    List<String> selectPermissionIdsByRoleId(String roleId);

    void saveRolePermissions(String roleId, List<String> permissionIds);
}
