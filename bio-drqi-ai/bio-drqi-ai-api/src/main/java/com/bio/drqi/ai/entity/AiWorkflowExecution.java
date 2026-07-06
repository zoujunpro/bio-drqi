package com.bio.drqi.ai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("ai_workflow_execution")
public class AiWorkflowExecution {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String executionNo;

    private Long workflowId;

    private String workflowCode;

    private String inputJson;

    private String outputJson;

    private String status;

    private String errorMessage;

    private Integer costMs;

    private Date createTime;

    private Date updateTime;
}
