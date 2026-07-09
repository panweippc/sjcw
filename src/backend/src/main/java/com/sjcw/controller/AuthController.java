package com.sjcw.controller;

import com.sjcw.common.JwtUtils;
import com.sjcw.common.MD5Utils;
import com.sjcw.common.Result;
import com.sjcw.entity.User;
import com.sjcw.service.MenuService;
import com.sjcw.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String loginName = params.get("loginName");
        String password = params.get("password");

        if (loginName == null || password == null || loginName.isEmpty() || password.isEmpty()) {
            return Result.error(400, "用户名或密码不能为空");
        }

        User user = userService.selectUserByLoginName(loginName);
        if (user == null) {
            return Result.error(401, "用户名不存在");
        }

        if (!user.getPassword().equalsIgnoreCase(MD5Utils.encrypt(password))) {
            return Result.error(401, "密码错误");
        }

        if (!user.getIsEnabled()) {
            return Result.error(401, "用户已禁用");
        }

        List<String> roleIds = userService.selectRoleIdsByUserId(user.getUserId());
        List<String> permissions = menuService.selectPermissionCodesByRoleIds(roleIds);
        List<Map<String, Object>> menus = buildMenuTree(menuService.selectMenuTreeByRoleIds(roleIds));

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("loginName", user.getLoginName());
        String token = jwtUtils.generateToken(claims);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);
        result.put("roles", roleIds);
        result.put("permissions", permissions);
        result.put("menus", menus);

        return Result.success(result);
    }

    @GetMapping("/getInfo")
    public Result<Map<String, Object>> getInfo(@RequestAttribute("userId") String userId) {
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error(401, "用户不存在");
        }

        List<String> roleIds = userService.selectRoleIdsByUserId(userId);
        List<String> permissions = menuService.selectPermissionCodesByRoleIds(roleIds);
        List<Map<String, Object>> menus = buildMenuTree(menuService.selectMenuTreeByRoleIds(roleIds));

        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("roles", roleIds);
        result.put("permissions", permissions);
        result.put("menus", menus);

        return Result.success(result);
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success();
    }

    private List<Map<String, Object>> buildMenuTree(List<com.sjcw.entity.Menu> menus) {
        return menus.stream().map(this::menuToMap).toList();
    }

    private Map<String, Object> menuToMap(com.sjcw.entity.Menu menu) {
        Map<String, Object> map = new HashMap<>();
        map.put("menuId", menu.getMenuId());
        map.put("menuName", menu.getMenuName());
        map.put("menuCode", menu.getMenuCode());
        map.put("parentId", menu.getParentId());
        map.put("menuLevel", menu.getMenuLevel());
        map.put("menuPath", menu.getMenuPath());
        map.put("icon", menu.getIcon());
        if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
            map.put("children", menu.getChildren().stream().map(this::menuToMap).toList());
        }
        return map;
    }
}
