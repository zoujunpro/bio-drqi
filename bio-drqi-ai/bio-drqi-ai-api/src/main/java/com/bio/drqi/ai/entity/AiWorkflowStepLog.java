package com.bio.drqi.ai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("ai_workflow_step_log")
public class AiWorkflowStepLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long executionId;

    private String nodeId;

    private String nodeType;

    private String nodeName;

    private String toolCode;

    private String inputJson;

    private String outputJson;

    private String status;

    private String errorMessage;

    private Integer costMs;

    private Date createTime;
}
