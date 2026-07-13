package com.bio.drqi.ai.dao.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * AI 文档检索权限快照。
 */
@TableName(value = "ai_document_permission")
@Data
public class AiDocumentPermission implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String documentId;

    private String principalType;

    private String principalId;

    private Integer canRead;

    private Date createTime;

    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
