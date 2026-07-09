package com.sjcw.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_import_record")
public class ImportRecord {

    @TableId(value = "import_id", type = IdType.ASSIGN_UUID)
    private String importId;

    @TableField("file_name")
    private String fileName;

    @TableField("file_path")
    private String filePath;

    @TableField("import_type")
    private String importType;

    @TableField("total_count")
    private Integer totalCount;

    @TableField("success_count")
    private Integer successCount;

    @TableField("fail_count")
    private Integer failCount;

    @TableField("status")
    private String status;

    @TableField("error_message")
    private String errorMessage;

    @TableField("imported_by")
    private String importedBy;

    @TableField("created_time")
    private LocalDateTime createdTime;

    @TableField("completed_time")
    private LocalDateTime completedTime;

    @TableField(exist = false)
    private String importedByName;
}
