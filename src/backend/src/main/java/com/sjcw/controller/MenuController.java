package com.sjcw.controller;

import com.sjcw.common.Result;
import com.sjcw.entity.Menu;
import com.sjcw.entity.Permission;
import com.sjcw.service.MenuService;
import com.sjcw.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @Autowired
    private PermissionService permissionService;

    @GetMapping("/tree")
    public Result<List<Menu>> getMenuTree() {
        List<Menu> menus = menuService.selectMenuTree();
        return Result.success(menus);
    }

    @GetMapping("/permissions")
    public Result<List<Permission>> getPermissions() {
        List<Permission> permissions = permissionService.list();
        return Result.success(permissions);
    }

    @GetMapping("/permissions/{menuId}")
    public Result<List<Permission>> getPermissionsByMenuId(@PathVariable String menuId) {
        List<Permission> permissions = permissionService.lambdaQuery()
                .eq(Permission::getMenuId, menuId)
                .list();
        return Result.success(permissions);
    }
}
