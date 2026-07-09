package com.sjcw.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sjcw.entity.User;

import java.util.List;

public interface UserService extends IService<User> {

    List<User> selectUserList(User user);

    User selectUserByLoginName(String loginName);

    List<String> selectRoleIdsByUserId(String userId);

    boolean saveUserWithRoles(User user, List<String> roleIds);

    boolean updateUserWithRoles(User user, List<String> roleIds);
}
