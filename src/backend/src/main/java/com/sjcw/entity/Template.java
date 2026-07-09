package com.sjcw.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_template")
public class Template {

    @TableId(value = "template_id", type = IdType.ASSIGN_UUID)
    private String templateId;

    @TableField("template_name")
    private String templateName;

    @TableField("template_code")
    private String templateCode;

    @TableField("template_type")
    private String templateType;

    @TableField("content")
    private String content;

    @TableField("description")
    private String description;

    @TableField("attachment")
    private String attachment;

    @TableField("is_enabled")
    private Boolean isEnabled;

    @TableField("sort_order")
    private Integer sortOrder;

    @TableField("created_time")
    private LocalDateTime createdTime;

    @TableField("updated_time")
    private LocalDateTime updatedTime;
}
