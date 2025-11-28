package com.bio.drqi.manage.sample.req;

import com.bio.drqi.manage.base.PageDTO;
import lombok.Data;

@Data
public class SampleTestByVectorTaskReqDTO extends PageDTO {
    private Integer vectorTaskId;
    private String sourceCode;

}
