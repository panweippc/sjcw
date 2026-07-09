package com.sjcw.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sjcw.entity.Menu;

import java.util.List;

public interface MenuService extends IService<Menu> {

    List<Menu> selectMenuTree();

    List<Menu> selectMenuTreeByRoleIds(List<String> roleIds);

    List<String> selectPermissionCodesByRoleIds(List<String> roleIds);
}
