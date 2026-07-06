package com.bio.drqi.ai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("ai_workflow_definition")
public class AiWorkflowDefinition {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String workflowCode;

    private String workflowName;

    private String description;

    private String category;

    private String dslJson;

    private Integer enabled;

    private Integer deleted;

    private Date createTime;

    private Date updateTime;
}
