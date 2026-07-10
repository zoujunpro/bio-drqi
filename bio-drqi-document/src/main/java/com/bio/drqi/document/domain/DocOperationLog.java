package com.bio.drqi.document.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 文档操作日志表
 * @TableName doc_operation_log
 */
@TableName(value ="doc_operation_log")
@Data
public class DocOperationLog implements Serializable {
    /**
     * 日志ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文档ID
     */
    private Long fileId;

    /**
     * 版本ID
     */
    private Long versionId;

    /**
     * 操作类型：UPLOAD/EDIT/DELETE/RESTORE/RENAME/ROLLBACK/PERMISSION/SHARE
     */
    private String operationType;

    /**
     * 操作内容
     */
    private String operationContent;

    /**
     * 修改前
     */
    private String beforeValue;

    /**
     * 修改后
     */
    private String afterValue;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作人名称
     */
    private String operatorName;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 浏览器信息
     */
    private String userAgent;

    /**
     * 操作时间
     */
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}