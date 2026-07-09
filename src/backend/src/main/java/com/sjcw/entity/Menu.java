package com.sjcw.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("sys_menu")
public class Menu {

    @TableId(value = "menu_id", type = IdType.ASSIGN_UUID)
    private String menuId;

    @TableField("menu_name")
    private String menuName;

    @TableField("menu_code")
    private String menuCode;

    @TableField("parent_id")
    private String parentId;

    @TableField("menu_level")
    private Integer menuLevel;

    @TableField("menu_path")
    private String menuPath;

    @TableField("icon")
    private String icon;

    @TableField("sort_order")
    private Integer sortOrder;

    @TableField("is_enabled")
    private Boolean isEnabled;

    @TableField("is_visible")
    private Boolean isVisible;

    @TableField("created_time")
    private LocalDateTime createdTime;

    @TableField("updated_time")
    private LocalDateTime updatedTime;

    @TableField(exist = false)
    private List<Menu> children;

    @TableField(exist = false)
    private List<Permission> permissions;
}
