package com.sjcw.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_role_permission")
public class RolePermission {

    @TableId("role_id")
    private String roleId;

    @TableField("permission_id")
    private String permissionId;

    @TableField("created_time")
    private LocalDateTime createdTime;
}
