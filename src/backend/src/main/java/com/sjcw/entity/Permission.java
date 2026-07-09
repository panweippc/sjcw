package com.sjcw.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_permission")
public class Permission {

    @TableId(value = "permission_id", type = IdType.ASSIGN_UUID)
    private String permissionId;

    @TableField("permission_name")
    private String permissionName;

    @TableField("permission_code")
    private String permissionCode;

    @TableField("menu_id")
    private String menuId;

    @TableField("description")
    private String description;

    @TableField("created_time")
    private LocalDateTime createdTime;
}
