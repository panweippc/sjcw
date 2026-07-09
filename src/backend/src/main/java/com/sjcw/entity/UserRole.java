package com.sjcw.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_user_role")
public class UserRole {

    @TableId("user_id")
    private String userId;

    @TableField("role_id")
    private String roleId;

    @TableField("created_time")
    private LocalDateTime createdTime;
}
