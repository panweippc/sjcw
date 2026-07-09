package com.sjcw.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sjcw.entity.Permission;
import com.sjcw.mapper.PermissionMapper;
import com.sjcw.service.PermissionService;
import org.springframework.stereotype.Service;

@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {
}
