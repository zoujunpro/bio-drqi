package com.bio.drqi.ai.dao.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * AI 任务模板步骤。
 */
@TableName(value = "ai_task_template_step")
@Data
public class AiTaskTemplateStep implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 模板编码。
     */
    private String templateCode;

    /**
     * 步骤顺序。
     */
    private Integer stepNo;

    /**
     * 任务编码。
     */
    private String taskCode;

    /**
     * 任务名称。
     */
    private String taskName;

    /**
     * 任务类型：QUERY/ANALYSIS/MERGE/DIFY/TOOL。
     */
    private String taskType;

    /**
     * 业务对象，例如 PROJECT、SAMPLE、PLANT。
     */
    private String businessObject;

    /**
     * 目标编码，通常是工具编码或 Dify 应用编码。
     */
    private String targetCode;

    /**
     * 必填参数 JSON 数组。
     */
    private String requiredParams;

    /**
     * 入参映射 JSON。
     */
    private String inputMapping;

    /**
     * 依赖步骤 JSON 数组。
     */
    private String dependsOn;

    /**
     * 状态：ACTIVE/DISABLED/DELETED。
     */
    private String status;

    private Date createTime;

    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
