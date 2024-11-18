package com.bio.cer.projecttask;

import com.bio.cer.base.PageDTO;
import lombok.Data;

import java.util.List;

@Data
public class ProjectTaskListPageReqDTO extends PageDTO {
    /**申请编号*/
    private String taskNum;

    private String taskTypeCode;

}
