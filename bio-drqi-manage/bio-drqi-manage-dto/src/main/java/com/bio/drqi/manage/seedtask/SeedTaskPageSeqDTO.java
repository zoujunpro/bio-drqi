package com.bio.drqi.manage.seedtask;

import com.bio.drqi.manage.base.PageDTO;
import lombok.Data;

@Data
public class SeedTaskPageSeqDTO extends PageDTO {

    /**申请编号*/
    private String taskNum;

    private String taskTypeCode;
}
