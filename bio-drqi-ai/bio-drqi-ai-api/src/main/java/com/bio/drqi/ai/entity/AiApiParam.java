package com.bio.drqi.ai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("ai_api_param")
public class AiApiParam {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String apiCode;

    private String paramName;

    private String paramType;

    private Integer required;

    private String javaField;

    private String businessName;

    private String aliases;

    private String sourceType;

    private String sourceField;

    private String defaultValue;

    private Integer aiEnabled;

    private Integer deleted;

    private Date syncTime;

    private Date createTime;

    private Date updateTime;
}
