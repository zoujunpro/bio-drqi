package com.bio.drqi.ai.dao.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * AI 任务模板。
 */
@TableName(value = "ai_task_template")
@Data
public class AiTaskTemplate implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 模板编码，例如 PROJECT_EXECUTION_ANALYSIS。
     */
    private String templateCode;

    /**
     * 模板名称。
     */
    private String templateName;

    /**
     * 关联意图编码。
     */
    private String intentCode;

    /**
     * 业务领域。
     */
    private String domain;

    /**
     * 模板说明。
     */
    private String description;

    /**
     * 状态：ACTIVE/DISABLED/DELETED。
     */
    private String status;

    private Date createTime;

    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
