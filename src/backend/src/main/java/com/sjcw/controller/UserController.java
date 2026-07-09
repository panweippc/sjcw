package com.sjcw.controller;

import com.sjcw.common.Result;
import com.sjcw.entity.User;
import com.sjcw.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public Result<List<User>> list(User user) {
        List<User> users = userService.selectUserList(user);
        return Result.success(users);
    }

    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable String id) {
        User user = userService.getById(id);
        if (user != null) {
            List<String> roleIds = userService.selectRoleIdsByUserId(id);
            user.setRoleIds(roleIds);
        }
        return Result.success(user);
    }

    @PostMapping
    public Result<Void> save(@RequestBody User user) {
        userService.saveUserWithRoles(user, user.getRoleIds());
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody User user) {
        userService.updateUserWithRoles(user, user.getRoleIds());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable String id) {
        userService.removeById(id);
        return Result.success();
    }

    @PutMapping("/resetPassword")
    public Result<Void> resetPassword(@RequestBody Map<String, String> params) {
        String userId = params.get("userId");
        String password = params.get("password");
        User user = userService.getById(userId);
        if (user != null) {
            user.setPassword(password);
            user.setUpdatedTime(LocalDateTime.now());
            userService.updateById(user);
        }
        return Result.success();
    }
}
