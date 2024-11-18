package com.bio.drqi.seedtask;

import com.bio.drqi.base.PageDTO;
import lombok.Data;

@Data
public class SeedTaskPageSeqDTO extends PageDTO {

    /**申请编号*/
    private String taskNum;

    private String taskTypeCode;
}
