package com.bio.drqi.ai.dao.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 企业业务意图定义。
 */
@TableName(value = "ai_intent")
@Data
public class AiIntent implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 意图唯一编码，例如 PROJECT_PROGRESS_QUERY。
     */
    private String intentCode;

    /**
     * 意图名称，例如 查询项目进度。
     */
    private String intentName;

    /**
     * 业务领域，例如 项目管理、CER、种子库。
     */
    private String domain;

    /**
     * 给模型和运营人员看的意图说明。
     */
    private String description;

    /**
     * 处理类型：TOOL/RAG/FILE/CHAT/WORKFLOW。
     */
    private String handlerType;

    /**
     * 状态：ACTIVE/DISABLED/DELETED。
     */
    private String status;

    private Date createTime;

    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
