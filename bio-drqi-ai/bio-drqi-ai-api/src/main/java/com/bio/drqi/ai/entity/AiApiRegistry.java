package com.bio.drqi.ai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("ai_api_registry")
public class AiApiRegistry {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String apiCode;

    private String serviceName;

    private String path;

    private String method;

    private String controllerClass;

    private String methodName;

    private String requestDto;

    private String responseType;

    private String apiName;

    private String description;

    private Integer aiEnabled;

    private Integer readOnly;

    private String riskLevel;

    private String ownerModule;

    private Integer deleted;

    private Date syncTime;

    private Date createTime;

    private Date updateTime;
}
