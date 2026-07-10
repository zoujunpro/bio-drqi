package com.bio.drqi.ai.dao.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * AI 可调用工具定义。
 */
@TableName(value = "ai_tool_definition")
@Data
public class AiToolDefinition implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 工具唯一编码，例如 queryProjectProgress。
     */
    private String toolCode;

    /**
     * 工具名称。
     */
    private String toolName;

    /**
     * 工具说明。
     */
    private String description;

    /**
     * 工具类型：API/WORKFLOW/DIFY/MCP/LOCAL。
     */
    private String toolType;

    /**
     * 目标编码，例如 ai_api_registry.api_code 或 workflow_code。
     */
    private String targetCode;

    /**
     * 入参 JSON Schema。
     */
    private String inputSchema;

    /**
     * 出参 JSON Schema。
     */
    private String outputSchema;

    /**
     * 企业服务地址或网关路径。
     */
    private String serviceUrl;

    /**
     * HTTP 方法：GET/POST/PUT/DELETE。
     */
    private String httpMethod;

    /**
     * 风险等级：LOW/MEDIUM/HIGH。
     */
    private String riskLevel;

    /**
     * 是否只读：1 是，0 否。
     */
    private Integer readOnly;

    /**
     * 状态：ACTIVE/DISABLED/DELETED。
     */
    private String status;

    private Date createTime;

    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
