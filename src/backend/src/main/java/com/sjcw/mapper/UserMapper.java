package com.sjcw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sjcw.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    List<User> selectUserList(User user);

    User selectUserByLoginName(@Param("loginName") String loginName);

    List<String> selectRoleIdsByUserId(@Param("userId") String userId);
}
