package com.bio.cer.project.req;

import com.bio.cer.base.PageDTO;
import lombok.Data;

@Data
public class ConversionAndTransDetailReqDTO extends PageDTO {
    /**
     * 移苗申请ID
     */
    private Integer id;
    /**
     * 实施方案ID
     */
    private Integer vectorTaskId;
}
