package com.bio.drqi.seedtask;

import com.easyflow.engine.model.SelfFlowActor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class SeedStartTaskReqDTO {
    /**工单类型*/
    @NotBlank(message = "工单类型参数缺失")
    private String taskType;
    /**任务工单描述*/
    @NotBlank(message = "任务工单描述参数缺失")
    private String taskDesc;
    /**任务表单*/
    @NotBlank(message = "任务表单参数缺失")
    private String formObject;

    private List<SelfFlowActor> selfFlowActorList;

}
