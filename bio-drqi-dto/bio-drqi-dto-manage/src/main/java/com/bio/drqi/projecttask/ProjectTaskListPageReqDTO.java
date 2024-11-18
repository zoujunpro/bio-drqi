package com.bio.drqi.projecttask;

import com.bio.drqi.base.PageDTO;
import lombok.Data;

@Data
public class ProjectTaskListPageReqDTO extends PageDTO {
    /**申请编号*/
    private String taskNum;

    private String taskTypeCode;

}
