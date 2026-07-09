package com.sjcw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sjcw.entity.Menu;
import com.sjcw.entity.Permission;
import com.sjcw.mapper.MenuMapper;
import com.sjcw.mapper.PermissionMapper;
import com.sjcw.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public List<Menu> selectMenuTree() {
        List<Menu> allMenus = baseMapper.selectMenuTree();
        List<Menu> tree = buildTree(allMenus);
        loadPermissions(tree);
        return tree;
    }

    @Override
    public List<Menu> selectMenuTreeByRoleIds(List<String> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<Menu> allMenus = baseMapper.selectMenuTreeByRoleIds(roleIds);
        List<Menu> tree = buildTree(allMenus);
        loadPermissions(tree);
        return tree;
    }

    @Override
    public List<String> selectPermissionCodesByRoleIds(List<String> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return new ArrayList<>();
        }
        return baseMapper.selectPermissionCodesByRoleIds(roleIds);
    }

    private List<Menu> buildTree(List<Menu> menus) {
        Map<String, Menu> menuMap = new HashMap<>();
        List<Menu> rootMenus = new ArrayList<>();

        for (Menu menu : menus) {
            menu.setChildren(new ArrayList<>());
            menuMap.put(menu.getMenuId(), menu);
        }

        for (Menu menu : menus) {
            if (menu.getParentId() == null || menu.getParentId().isEmpty()) {
                rootMenus.add(menu);
            } else {
                Menu parent = menuMap.get(menu.getParentId());
                if (parent != null) {
                    parent.getChildren().add(menu);
                }
            }
        }

        return rootMenus;
    }

    private void loadPermissions(List<Menu> menus) {
        for (Menu menu : menus) {
            LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Permission::getMenuId, menu.getMenuId());
            List<Permission> permissions = permissionMapper.selectList(wrapper);
            menu.setPermissions(permissions);
            if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
                loadPermissions(menu.getChildren());
            }
        }
    }
}
