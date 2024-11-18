package com.bio.drqi.task;

import com.bio.drqi.base.PageDTO;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class BioTaskListPageReqDTO extends PageDTO {
    /**申请编号*/
    private String taskNum;

    private String taskTypeCode;
    /**
     *
     */
    @NotBlank(message = "参数缺失 任务类别")
    private String taskCategory;

}
