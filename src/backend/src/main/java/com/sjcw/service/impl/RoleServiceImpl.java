package com.sjcw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sjcw.entity.Role;
import com.sjcw.entity.RolePermission;
import com.sjcw.mapper.RoleMapper;
import com.sjcw.mapper.RolePermissionMapper;
import com.sjcw.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Override
    public List<Role> selectRoleList(Role role) {
        return baseMapper.selectRoleList(role);
    }

    @Override
    public List<String> selectPermissionIdsByRoleId(String roleId) {
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId, roleId);
        List<RolePermission> rolePermissions = rolePermissionMapper.selectList(wrapper);
        List<String> permissionIds = new ArrayList<>();
        for (RolePermission rp : rolePermissions) {
            permissionIds.add(rp.getPermissionId());
        }
        return permissionIds;
    }

    @Override
    @Transactional
    public void saveRolePermissions(String roleId, List<String> permissionIds) {
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId, roleId);
        rolePermissionMapper.delete(wrapper);
        
        if (permissionIds != null && !permissionIds.isEmpty()) {
            for (String permissionId : permissionIds) {
                RolePermission rolePermission = new RolePermission();
                rolePermission.setRoleId(roleId);
                rolePermission.setPermissionId(permissionId);
                rolePermission.setCreatedTime(LocalDateTime.now());
                rolePermissionMapper.insert(rolePermission);
            }
        }
    }
}
