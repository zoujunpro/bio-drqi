package com.bio.flow.dto;

import com.easyflow.engine.model.SelfFlowActor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class BioTaskTemporarySaveReqDTO {

    /**
     * 工单类型
     */
    private Integer id;
    /**
     * 任务表单
     */
    private String formObject;


    /**工单类型*/
    private String taskType;


    /**任务工单描述*/
    private String taskDesc;

    /**
     * 关联工单
     */
    private String refTaskNum;


}
