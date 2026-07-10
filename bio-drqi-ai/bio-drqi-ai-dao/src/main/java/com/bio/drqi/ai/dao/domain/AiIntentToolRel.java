package com.bio.drqi.ai.dao.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 意图和工具关系。
 */
@TableName(value = "ai_intent_tool_rel")
@Data
public class AiIntentToolRel implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联 ai_intent.intent_code。
     */
    private String intentCode;

    /**
     * 关联 ai_tool_definition.tool_code。
     */
    private String toolCode;

    /**
     * 多工具编排顺序，数字越小越优先。
     */
    private Integer priority;

    /**
     * 状态：ACTIVE/DISABLED/DELETED。
     */
    private String status;

    private Date createTime;

    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
