package com.sjcw.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sjcw.entity.User;
import com.sjcw.entity.UserRole;
import com.sjcw.mapper.UserMapper;
import com.sjcw.mapper.UserRoleMapper;
import com.sjcw.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public List<User> selectUserList(User user) {
        return baseMapper.selectUserList(user);
    }

    @Override
    public User selectUserByLoginName(String loginName) {
        return baseMapper.selectUserByLoginName(loginName);
    }

    @Override
    public List<String> selectRoleIdsByUserId(String userId) {
        return baseMapper.selectRoleIdsByUserId(userId);
    }

    @Override
    @Transactional
    public boolean saveUserWithRoles(User user, List<String> roleIds) {
        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());
        if (save(user)) {
            if (roleIds != null && !roleIds.isEmpty()) {
                List<UserRole> userRoles = new ArrayList<>();
                for (String roleId : roleIds) {
                    UserRole userRole = new UserRole();
                    userRole.setUserId(user.getUserId());
                    userRole.setRoleId(roleId);
                    userRoles.add(userRole);
                }
                userRoleMapper.batchInsert(userRoles);
            }
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean updateUserWithRoles(User user, List<String> roleIds) {
        user.setUpdatedTime(LocalDateTime.now());
        if (updateById(user)) {
            userRoleMapper.deleteByUserId(user.getUserId());
            if (roleIds != null && !roleIds.isEmpty()) {
                List<UserRole> userRoles = new ArrayList<>();
                for (String roleId : roleIds) {
                    UserRole userRole = new UserRole();
                    userRole.setUserId(user.getUserId());
                    userRole.setRoleId(roleId);
                    userRoles.add(userRole);
                }
                userRoleMapper.batchInsert(userRoles);
            }
            return true;
        }
        return false;
    }
}
