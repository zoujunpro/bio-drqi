package com.bio.drqi.ai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("ai_intent_keyword")
public class AiIntentKeyword {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String intent;

    private String keyword;

    private Integer weight;

    private Integer enabled;

    private Date createTime;

    private Date updateTime;
}
