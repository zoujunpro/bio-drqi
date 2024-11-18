package com.bio.drqi.projecttask;

import com.easyflow.engine.model.SelfFlowActor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;


@Data
public class ProjectReStartTaskReqDTO {
    /**工单类型*/
    @NotNull(message = "任务主键缺失")
    private Integer id;
    /**任务表单*/
    @NotBlank(message = "任务表单参数缺失")
    private String formObject;

    /**任务工单描述*/
    @NotBlank(message = "任务工单描述参数缺失")
    private String taskDesc;

    /**
     * 关联工单
     */
    private String refTaskNum;



    private List<SelfFlowActor> selfFlowActorList;


}
