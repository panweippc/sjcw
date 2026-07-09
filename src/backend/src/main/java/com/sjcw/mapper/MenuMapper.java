package com.sjcw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sjcw.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

    List<Menu> selectMenuTree();

    List<Menu> selectMenuTreeByRoleIds(@Param("roleIds") List<String> roleIds);

    List<String> selectPermissionCodesByRoleIds(@Param("roleIds") List<String> roleIds);
}
