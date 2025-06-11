package com.bio.flow.dto;

import com.easyflow.engine.model.SelfFlowActor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BioTaskStartReqDTO {

    private Integer id;
    /**工单类型*/
    @NotBlank(message = "工单类型参数缺失")
    private String taskType;
    /**任务工单描述*/
    @NotBlank(message = "任务工单描述参数缺失")
    private String taskDesc;

    /**任务表单*/
    @NotBlank(message = "任务表单参数缺失")
    private String formObject;
    /**
     * 关联工单
     */
    private String refTaskNum;

    private List<SelfFlowActor> selfFlowActorList;


}
