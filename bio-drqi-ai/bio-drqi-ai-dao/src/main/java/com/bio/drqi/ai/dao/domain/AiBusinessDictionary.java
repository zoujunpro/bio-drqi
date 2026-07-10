package com.bio.drqi.ai.dao.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * AI 业务词典。
 */
@TableName(value = "ai_business_dictionary")
@Data
public class AiBusinessDictionary implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 词典类型，例如 PROJECT、CROP、BASE、DEPARTMENT。
     */
    private String dictType;

    /**
     * 业务编码。
     */
    private String dictCode;

    /**
     * 标准名称。
     */
    private String dictName;

    /**
     * 别名，多个用英文逗号分隔。
     */
    private String aliases;

    /**
     * 业务领域。
     */
    private String domain;

    /**
     * 状态：ACTIVE/DISABLED/DELETED。
     */
    private String status;

    private Date createTime;

    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
