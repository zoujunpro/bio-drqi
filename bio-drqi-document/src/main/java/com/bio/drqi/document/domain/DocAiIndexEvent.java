package com.bio.drqi.document.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 文档 AI 索引事件表。
 * 用 Outbox 记录文档生命周期变化，避免上传事务直接依赖 AI 服务。
 */
@TableName(value = "doc_ai_index_event")
@Data
public class DocAiIndexEvent implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 事件类型：DOCUMENT_UPLOADED/VERSION_UPLOADED/DOCUMENT_DELETED/PERMISSION_CHANGED
     */
    private String eventType;

    private Long documentId;

    private Long versionId;

    private String docCode;

    private String docName;

    private String filePath;

    private String fileType;

    private String status;

    private Integer retryCount;

    private String errorMessage;

    private Date createTime;

    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
