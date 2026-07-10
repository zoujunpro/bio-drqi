package com.bio.drqi.ai.dao.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 业务意图的用户表达样例。
 */
@TableName(value = "ai_intent_example")
@Data
public class AiIntentExample implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联 ai_intent.intent_code。
     */
    private String intentCode;

    /**
     * 真实用户可能说的话。
     */
    private String exampleText;

    /**
     * 状态：ACTIVE/DISABLED/DELETED。
     */
    private String status;

    private Date createTime;

    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
