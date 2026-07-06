package com.bio.drqi.ai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("ai_business_term")
public class AiBusinessTerm {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String phrase;

    private String domain;

    private String meaning;

    private String metric;

    private String field;

    private Integer enabled;

    private Date createTime;

    private Date updateTime;
}
