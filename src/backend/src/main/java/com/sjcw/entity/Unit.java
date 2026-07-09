package com.sjcw.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("sys_unit")
public class Unit {

    @TableId(value = "unit_id", type = IdType.ASSIGN_UUID)
    private String unitId;

    @TableField("unit_name")
    private String unitName;

    @TableField("unit_code")
    private String unitCode;

    @TableField("parent_id")
    private String parentId;

    @TableField("unit_level")
    private Integer unitLevel;

    @TableField("unit_path")
    private String unitPath;

    @TableField("address")
    private String address;

    @TableField("phone")
    private String phone;

    @TableField("contact_person")
    private String contactPerson;

    @TableField("description")
    private String description;

    @TableField("is_enabled")
    private Boolean isEnabled;

    @TableField("sort_order")
    private Integer sortOrder;

    @TableField("created_time")
    private LocalDateTime createdTime;

    @TableField("updated_time")
    private LocalDateTime updatedTime;

    @TableField(exist = false)
    private List<Unit> children;
}
