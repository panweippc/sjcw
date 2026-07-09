package com.sjcw.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_role_menu")
public class RoleMenu {

    @TableId("role_id")
    private String roleId;

    @TableField("menu_id")
    private String menuId;

    @TableField("created_time")
    private LocalDateTime createdTime;
}
